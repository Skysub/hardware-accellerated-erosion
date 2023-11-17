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

  //Modules
  val registerFile = Module(new RegisterFile())
  val decider = Module(new Decider())

  //Registers
  val edgeReg = RegInit(400.U(16.W))
  val xPosReg = RegInit(1.U(16.W))
  val yPosReg = RegInit(1.U(16.W))
  val forwardReg = RegInit(0.U(4.W))
  val edgeGoingOn = RegInit(1.U(1.W))

  //Default values
  io.writeEnable := false.B
  io.address := 0.U(16.W)
  io.done := false.B
  io.dataWrite := 1337.U(32.W)

  registerFile.io.xPosition := xPosReg
  registerFile.io.shiftBanks := false.B
  registerFile.io.regWrite := false.B
  registerFile.io.dataIn := 1.U
  registerFile.io.writeWhere := 1.U

  decider.io.forward := registerFile.io.forwardOne
  decider.io.here := registerFile.io.here
  decider.io.downward := registerFile.io.downward
  decider.io.backward := registerFile.io.backward
  decider.io.upward := registerFile.io.upward
  decider.io.yPos := yPosReg

  //FSMD switch
  switch(mainStateReg) {
    is(idle) {
      when(io.start) {
        mainStateReg := write
        writeStateReg := edge
        edgeReg := 400.U(16.W)
        edgeGoingOn := 1.U
      }
    }

    is(read) {
      registerFile.io.regWrite := true.B
      registerFile.io.dataIn := io.dataRead
      switch(readStateReg){
        is(forward){
          io.address := ((yPosReg) * 20.U) + xPosReg + 1.U
          registerFile.io.writeWhere := 0.U

          decider.io.forward := io.dataRead
        }
        is(here){
          io.address := ((yPosReg) * 20.U) + xPosReg
          registerFile.io.writeWhere := 1.U

          decider.io.here := io.dataRead
        }
        is(downward){
          io.address := ((yPosReg + 1.U) * 20.U) + xPosReg
          registerFile.io.writeWhere := 2.U

          decider.io.downward := io.dataRead
        }
        is(backward){
          io.address := ((yPosReg) * 20.U) + xPosReg - 1.U
          registerFile.io.writeWhere := 3.U

          decider.io.backward := io.dataRead
        }
        is(upward){
          io.address := ((yPosReg - 1.U) * 20.U) + xPosReg
          registerFile.io.writeWhere := 4.U

          decider.io.upward := io.dataRead
        }
      }
    }

    is(write) {
      io.writeEnable := true.B
      io.dataWrite := 0.U

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

        }
        is(forwardOne) {
          forwardReg := Mux(forwardReg === 0.U, 0.U, forwardReg - 1.U)
        }
        is(forwardTwo) {
          forwardReg := Mux(forwardReg === 0.U, 0.U, forwardReg - 1.U)
        }

        //The edge states
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
            //mainStateReg := done //FINISH AFTER EDGE HAS BEEN WRITTEN (DEBUG)
          }
        } //Edge state done
      }
    }

    is(done) {
      io.done := true.B
      mainStateReg := done
    }
  }

  //The decider is silent when setting the edge to black or when setting multiple pixels in front to black
  when(edgeGoingOn === 0.U && forwardReg === 0.U) {
    //Change states and substates
    switch(decider.io.nextState) {
      is(0.U) {
        mainStateReg := write
        writeStateReg := writeWhiteHere

        when(xPosReg === 18.U) {
          xPosReg := 1.U
          yPosReg := yPosReg + 1.U
          registerFile.io.shiftBanks := true.B
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
          registerFile.io.shiftBanks := true.B
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
          registerFile.io.shiftBanks := true.B
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
          registerFile.io.shiftBanks := true.B
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
}
