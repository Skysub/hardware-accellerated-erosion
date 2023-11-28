import chisel3._
import chisel3.util._

class Accelerator extends Module {
  val io = IO(new Bundle {
    val start = Input(Bool())
    val done = Output(Bool())

    val address = Output(UInt (16.W))
    val dataRead = Input(UInt (32.W))
    val writeEnable = Output(Bool ())
    val dataWrite = Output(UInt (32.W))

  })

  //State enum and register
  val idle :: done :: write :: read :: Nil = Enum(4) //main states
  val here :: forward :: backward :: upward :: downward :: Nil = Enum(5) //read substates
  val edge :: writeWhiteHere :: writeBlackHere :: forwardOne :: forwardTwo :: Nil = Enum(5) //write substates
  val edgeTop :: edgeBottom :: edgeLeft :: edgeRight :: Nil = Enum(4) //Edge subsubstates
  val mainStateReg = RegInit(idle)
  val readStateReg = RegInit(forward)
  val writeStateReg = RegInit(edge)
  val edgeStateReg = RegInit(edgeTop)

  //Registers
  val edgeReg = RegInit(400.U(16.W)) //Used to hold the address we're at when doing the edge
  val xPosReg = RegInit(1.U(16.W)) //Our current location's x position
  val yPosReg = RegInit(1.U(16.W)) //Our current location's y position
  val forwardReg = RegInit(0.U(4.W)) //Counts the number of cycles left before the write forward one/two states are done
  val edgeGoingOn = RegInit(1.U(1.W)) //Disables certain logic when the edge is being worked on

  //Default values
  io.writeEnable := false.B
  io.address := 0.U(16.W)
  io.done := false.B
  io.dataWrite := 1.U(32.W)

  //Registerfile
  //Created from submodule

  //Initialize registers
  val registerBankA = RegInit(VecInit(Seq.fill(20)(1.U(8.W))))
  val registerBankB = RegInit(VecInit(Seq.fill(20)(1.U(8.W))))
  val registerBankC = RegInit(VecInit(Seq.fill(20)(1.U(8.W))))
  val bankRotationReg = RegInit((0.U(4.W)))

  val rforwardOne = Wire(UInt(4.W))
  val rhere = Wire(UInt(4.W))
  val rdownward = Wire(UInt(4.W))
  val rbackward = Wire(UInt(4.W))
  val rupward = Wire(UInt(4.W))

  val regWrite = Wire(Bool())
  val rdataIn = Wire(UInt(8.W))
  val rwriteWhere = Wire(UInt(4.W))
  val shiftBanks = Wire(Bool())

  //Defaults for registerfile
  rforwardOne := 1.U
  rhere := 1.U
  rdownward := 1.U
  rbackward := 1.U
  rupward := 1.U

  rdataIn := 1.U
  rwriteWhere := 1.U
  regWrite := false.B
  shiftBanks := false.B

  //Eliminating the decider module and intregrating into the accelerator module
  val dnextState = Wire(UInt(4.W))
  val dforward = Wire(UInt(8.W))
  val dhere = Wire(UInt(8.W))
  val ddownward = Wire(UInt(8.W))
  val dbackward = Wire(UInt(8.W))
  val dupward = Wire(UInt(8.W))

  dforward := rforwardOne
  dhere := rhere
  ddownward := rdownward
  dbackward := rbackward
  dupward := rupward

  //FSMD switch
  switch(mainStateReg) {
    is(idle) {
      when(io.start) {
        mainStateReg := write //We start by doing the edge
        writeStateReg := edge
        edgeReg := 400.U(16.W)
        edgeGoingOn := 1.U
      }
    }

    is(read) {
      regWrite := true.B
      rdataIn := io.dataRead
      switch(readStateReg){
        is(forward){
          io.address := ((yPosReg) * 20.U) + xPosReg + 1.U //Calculates where in memory 'forward' is
          rwriteWhere := 0.U //Tells the registerfile what position we're writing to
          dforward := io.dataRead //Plugs the new value directly into the decider instead of waiting for the register to update
        }
        is(here){
          io.address := ((yPosReg) * 20.U) + xPosReg
          rwriteWhere := 1.U
          dhere := io.dataRead
        }
        is(downward){
          io.address := ((yPosReg + 1.U) * 20.U) + xPosReg
          rwriteWhere := 2.U
          ddownward := io.dataRead
        }
        is(backward){
          io.address := ((yPosReg) * 20.U) + xPosReg - 1.U
          rwriteWhere := 3.U
          dbackward := io.dataRead
        }
        is(upward){
          io.address := ((yPosReg - 1.U) * 20.U) + xPosReg
          rwriteWhere := 4.U
          dupward := io.dataRead
        }
      }
    }

    is(write) {
      io.writeEnable := true.B
      io.dataWrite := 0.U //By default we write black pixels

      //When writing, we're currently ahead of where we've determined a value should be written
      //This when statement corrects for row changes so we correctly write to the location 'behind' us
      when(xPosReg === 1.U) {
        io.address := ((yPosReg - 1.U) * 20.U) + 18.U + 400.U - forwardReg
      }.otherwise {
        io.address := ((yPosReg) * 20.U) + xPosReg - 1.U + 400.U - forwardReg
      }

      switch(writeStateReg){
        is(writeWhiteHere) {
          io.dataWrite := 255.U
        }
        is(writeBlackHere){
          //No additional behavior needed
        }
        //When writing forward whe need to decrement the forwardReg each cycle
        is(forwardOne) {
          forwardReg := Mux(forwardReg === 0.U, 0.U, forwardReg - 1.U)
        }
        is(forwardTwo) {
          forwardReg := Mux(forwardReg === 0.U, 0.U, forwardReg - 1.U)
        }

        //The edge states
        //The substates determine by how much the address is incremented and how/when we change to a different edge state
        is(edge) {
          io.address := edgeReg
          io.dataWrite := 0.U(32.W)
          switch(edgeStateReg) {
            is(edgeTop) {
              when(edgeReg === 419.U(16.W)) {
                edgeStateReg := edgeLeft
                edgeReg := 420.U(16.W)
              }.otherwise {
                edgeReg := edgeReg + 1.U
              }
            }
            is(edgeLeft) {
              when(edgeReg === 760.U(16.W)) {
                edgeStateReg := edgeRight
                edgeReg := 439.U(16.W)
              }.otherwise {
                edgeReg := edgeReg + 20.U
              }
            }
            is(edgeRight) {
              when(edgeReg === 779.U(16.W)) {
                edgeStateReg := edgeTop
                edgeReg := 780.U(16.W)
              }.otherwise {
                edgeReg := edgeReg + 20.U
              }
            }
            is(edgeBottom) {
              edgeReg := edgeReg + 1.U
            }
          }

          when(edgeReg === 799.U) {
            mainStateReg := read
            readStateReg := forward
            edgeGoingOn := 0.U
          }
        } //Edge state done
      }
    }
  }



  //The deciders logic
  //Eliminating the decider module and intregrating into the accelerator module

  //Default value, when no pixels are black and no pixels have the unknown value (1)
  dnextState := 0.U //Write white here

  //The decider
  //Checks for unknown values
  when(dupward === 1.U) {
    dnextState := 8.U //Read the upward value
  }
  when(dbackward === 1.U) {
    dnextState := 7.U //Read the backward value
  }
  when(ddownward === 1.U) {
    dnextState := 6.U //Read the downward value
  }
  when(dhere === 1.U) {
    dnextState := 5.U //Read value here
  }
  when(dforward === 1.U) {
    dnextState := 4.U //Read the forward value
  }

  //Checks for any black pixels
  when(dupward === 0.U) {
    dnextState := 3.U //Write here black
  }
  when(dbackward === 0.U) {
    dnextState := 3.U //Write here black
  }
  when(ddownward === 0.U) {
    dnextState := 3.U //Write here black
  }

  //Write foward one and two are disabled on the final row so that we don't miss the final pixel
  when(dhere === 0.U) {
    when(yPosReg === 18.U) {
      dnextState := 3.U //Write here black
    }.otherwise {
      dnextState := 2.U //Write forward one
    }
  }
  when(dforward === 0.U) {
    when(yPosReg === 18.U) {
      dnextState := 3.U //Write here black
    }.otherwise {
      dnextState := 1.U //Write forward two
    }
  }

  //The decider is silent when setting the edge to black or when setting multiple pixels in front to black
  when(edgeGoingOn === 0.U && forwardReg === 0.U) {
    //Change states and substates
    switch(dnextState) {
      is(0.U) {
        mainStateReg := write
        writeStateReg := writeWhiteHere

        when(xPosReg === 18.U) {
          xPosReg := 1.U
          yPosReg := yPosReg + 1.U
          shiftBanks := true.B
        }.otherwise {
          xPosReg := xPosReg + 1.U
        }
      }
      is(1.U) {
        mainStateReg := write
        writeStateReg := forwardTwo

        when(xPosReg >= 16.U) {
          xPosReg := 1.U
          yPosReg := yPosReg + 1.U
          shiftBanks := true.B
          when(xPosReg >= 17.U){
            forwardReg := 0.U
          } .otherwise{
            forwardReg := 1.U
          }

        }.otherwise {
          xPosReg := xPosReg + 3.U
          forwardReg := 2.U
        }
      }
      is(2.U) {
        mainStateReg := write
        writeStateReg := forwardOne

        when(xPosReg >= 17.U) {
          xPosReg := 1.U
          yPosReg := yPosReg + 1.U
          shiftBanks := true.B
          forwardReg := 0.U
        }.otherwise {
          xPosReg := xPosReg + 2.U
          forwardReg := 1.U
        }
      }
      is(3.U) {
        mainStateReg := write
        writeStateReg := writeBlackHere

        when(xPosReg === 18.U) {
          xPosReg := 1.U
          yPosReg := yPosReg + 1.U
          shiftBanks := true.B
        }.otherwise {
          xPosReg := xPosReg + 1.U
        }
      }
      is(4.U) {
        mainStateReg := read
        readStateReg := forward
      }
      is(5.U) {
        mainStateReg := read
        readStateReg := here
      }
      is(6.U) {
        mainStateReg := read
        readStateReg := downward
      }
      is(7.U) {
        mainStateReg := read
        readStateReg := backward
      }
      is(8.U) {
        mainStateReg := read
        readStateReg := upward
      }
    }
  }

  when(((yPosReg * 20.U) + xPosReg) === 378.U) { //At the final pixel in possible white space
    mainStateReg := done
    io.done := true.B
  }


  //Register file from the submodule
  //Clamp the bank rotation register value
  when(shiftBanks) {
    bankRotationReg := Mux(bankRotationReg === 2.U, 0.U, bankRotationReg + 1.U)
  }

  //Sets the positional outputs to the correct register while accounting for the x-pos and bankRotation
  switch(bankRotationReg) {
    is(0.U) {
      rupward := registerBankA(xPosReg)
      rhere := registerBankB(xPosReg)
      rdownward := registerBankC(xPosReg)
    }
    is(1.U) {
      rupward := registerBankB(xPosReg)
      rhere := registerBankC(xPosReg)
      rdownward := registerBankA(xPosReg)
    }
    is(2.U) {
      rupward := registerBankC(xPosReg)
      rhere := registerBankA(xPosReg)
      rdownward := registerBankB(xPosReg)
    }
  }

  //Same as above, except it doesn't output an  actual value when we're too early in the row
  when(xPosReg === 0.U) {
    rbackward := 1.U
  }.otherwise {
    switch(bankRotationReg) {
      is(0.U) {
        rbackward := registerBankB(xPosReg - 1.U)
      }
      is(1.U) {
        rbackward := registerBankC(xPosReg - 1.U)
      }
      is(2.U) {
        rbackward := registerBankA(xPosReg - 1.U)
      }
    }
  }

  when(xPosReg === 19.U) {
    rforwardOne := 1.U
  }.otherwise {
    switch(bankRotationReg) {
      is(0.U) {
        rforwardOne := registerBankB(xPosReg + 1.U)
      }
      is(1.U) {
        rforwardOne := registerBankC(xPosReg + 1.U)
      }
      is(2.U) {
        rforwardOne := registerBankA(xPosReg + 1.U)
      }
    }
  }

  //Where to write the newly read number?
  when(regWrite) {
    switch(rwriteWhere) {
      is(0.U) { //forwardOne
        switch(bankRotationReg) {
          is(0.U) {
            registerBankB(xPosReg + 1.U) := rdataIn
          }
          is(1.U) {
            registerBankC(xPosReg + 1.U) := rdataIn
          }
          is(2.U) {
            registerBankA(xPosReg + 1.U) := rdataIn
          }
        }
      }
      is(1.U) { //Here
        switch(bankRotationReg) {
          is(0.U) {
            registerBankB(xPosReg) := rdataIn
          }
          is(1.U) {
            registerBankC(xPosReg) := rdataIn
          }
          is(2.U) {
            registerBankA(xPosReg) := rdataIn
          }
        }
      }
      is(2.U) { //Downward
        switch(bankRotationReg) {
          is(0.U) {
            registerBankC(xPosReg) := rdataIn
          }
          is(1.U) {
            registerBankA(xPosReg) := rdataIn
          }
          is(2.U) {
            registerBankB(xPosReg) := rdataIn
          }
        }
      }
      is(3.U) { //Backwards
        switch(bankRotationReg) {
          is(0.U) {
            registerBankB(xPosReg - 1.U) := rdataIn
          }
          is(1.U) {
            registerBankC(xPosReg - 1.U) := rdataIn
          }
          is(2.U) {
            registerBankA(xPosReg - 1.U) := rdataIn
          }
        }
      }
      is(4.U) { //Upward
        switch(bankRotationReg) {
          is(0.U) {
            registerBankA(xPosReg) := rdataIn
          }
          is(1.U) {
            registerBankB(xPosReg) := rdataIn
          }
          is(2.U) {
            registerBankC(xPosReg) := rdataIn
          }
        }
      }
      is(5.U) { //forwardTwo
        switch(bankRotationReg) {
          is(0.U) {
            registerBankB(xPosReg + 2.U) := rdataIn
          }
          is(1.U) {
            registerBankC(xPosReg + 2.U) := rdataIn
          }
          is(2.U) {
            registerBankA(xPosReg + 2.U) := rdataIn
          }
        }
      }
    }
  }

  //Clears the appropriate registerbank
  when(shiftBanks) {
    switch(bankRotationReg) {
      is(0.U) {
        registerBankA(0) := 1.U
        registerBankA(1) := 1.U
        registerBankA(2) := 1.U
        registerBankA(3) := 1.U
        registerBankA(4) := 1.U
        registerBankA(5) := 1.U
        registerBankA(6) := 1.U
        registerBankA(7) := 1.U
        registerBankA(8) := 1.U
        registerBankA(9) := 1.U
        registerBankA(10) := 1.U
        registerBankA(11) := 1.U
        registerBankA(12) := 1.U
        registerBankA(13) := 1.U
        registerBankA(14) := 1.U
        registerBankA(15) := 1.U
        registerBankA(16) := 1.U
        registerBankA(17) := 1.U
        registerBankA(18) := 1.U
        registerBankA(19) := 1.U
      }
      is(1.U) {
        registerBankB(0) := 1.U
        registerBankB(1) := 1.U
        registerBankB(2) := 1.U
        registerBankB(3) := 1.U
        registerBankB(4) := 1.U
        registerBankB(5) := 1.U
        registerBankB(6) := 1.U
        registerBankB(7) := 1.U
        registerBankB(8) := 1.U
        registerBankB(9) := 1.U
        registerBankB(10) := 1.U
        registerBankB(11) := 1.U
        registerBankB(12) := 1.U
        registerBankB(13) := 1.U
        registerBankB(14) := 1.U
        registerBankB(15) := 1.U
        registerBankB(16) := 1.U
        registerBankB(17) := 1.U
        registerBankB(18) := 1.U
        registerBankB(19) := 1.U
      }
      is(2.U) {
        registerBankC(0) := 1.U
        registerBankC(1) := 1.U
        registerBankC(2) := 1.U
        registerBankC(3) := 1.U
        registerBankC(4) := 1.U
        registerBankC(5) := 1.U
        registerBankC(6) := 1.U
        registerBankC(7) := 1.U
        registerBankC(8) := 1.U
        registerBankC(9) := 1.U
        registerBankC(10) := 1.U
        registerBankC(11) := 1.U
        registerBankC(12) := 1.U
        registerBankC(13) := 1.U
        registerBankC(14) := 1.U
        registerBankC(15) := 1.U
        registerBankC(16) := 1.U
        registerBankC(17) := 1.U
        registerBankC(18) := 1.U
        registerBankC(19) := 1.U
      }
    }
  }


}
