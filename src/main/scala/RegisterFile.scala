import chisel3._
import chisel3.util._

class RegisterFile extends Module {
  val io = IO(new Bundle {
    val dataIn = Input(UInt(8.W)) //Data from memory
    val xPosition = Input(UInt(16.W)) //The x-posiotion we're currently at/examining

    //The positional outputs
    val forwardOne = Output(UInt(8.W))
    val here = Output(UInt(8.W))
    val downward = Output(UInt(8.W))
    val backward = Output(UInt(8.W))
    val upward = Output(UInt(8.W))

    //Control
    val regWrite = Input(Bool()) //Write to a register?
    val writeWhere = Input(UInt(4.W)) //What position should we write the value to in the order defined above
    val shiftBanks = Input(Bool()) //Rotates the register banks and clears the last
  })

  //Initialize registers
  val registerBankA =  RegInit(VecInit(Seq.fill(20)(1.U(8.W))))
  val registerBankB =  RegInit(VecInit(Seq.fill(20)(1.U(8.W))))
  val registerBankC =  RegInit(VecInit(Seq.fill(20)(1.U(8.W))))
  val bankRotationReg = RegInit((0.U(4.W)))

  //Setting default values
  io.forwardOne := 1.U
  io.here := 1.U
  io.downward := 1.U
  io.backward := 1.U
  io.upward := 1.U

  //Clamp the bank rotation register value
  when(io.shiftBanks){
    bankRotationReg := Mux(bankRotationReg === 2.U, 0.U, bankRotationReg + 1.U)
  }

  //Sets the positional outputs to the correct register while accounting for the x-pos and bankRotation
  switch(bankRotationReg){
    is(0.U){
      io.upward := registerBankA(io.xPosition)
      io.here := registerBankB(io.xPosition)
      io.downward := registerBankC(io.xPosition)
    }
    is(1.U) {
      io.upward := registerBankB(io.xPosition)
      io.here := registerBankC(io.xPosition)
      io.downward := registerBankA(io.xPosition)
    }
    is(2.U) {
      io.upward := registerBankC(io.xPosition)
      io.here := registerBankA(io.xPosition)
      io.downward := registerBankB(io.xPosition)
    }
  }

  //Same as above, except it doesn't output an  actual value when we're too early in the row
  when(io.xPosition === 0.U){
    io.backward := 1.U
  } .otherwise {
    switch(bankRotationReg) {
      is(0.U) {
        io.backward := registerBankB(io.xPosition - 1.U)
      }
      is(1.U) {
        io.backward := registerBankC(io.xPosition - 1.U)
      }
      is(2.U) {
        io.backward := registerBankA(io.xPosition - 1.U)
      }
    }
  }

  when(io.xPosition === 19.U) {
    io.forwardOne := 1.U
  }.otherwise {
    switch(bankRotationReg) {
      is(0.U) {
        io.forwardOne := registerBankB(io.xPosition + 1.U)
      }
      is(1.U) {
        io.forwardOne := registerBankC(io.xPosition + 1.U)
      }
      is(2.U) {
        io.forwardOne := registerBankA(io.xPosition + 1.U)
      }
    }
  }

  //Where to write the newly read number?
  when(io.regWrite) {
    switch(io.writeWhere){
      is(0.U){ //forwardOne
        switch(bankRotationReg){
          is(0.U){
            registerBankB(io.xPosition + 1.U) := io.dataIn
          }
          is(1.U) {
            registerBankC(io.xPosition + 1.U) := io.dataIn
          }
          is(2.U) {
            registerBankA(io.xPosition + 1.U) := io.dataIn
          }
        }
      }
      is(1.U) { //Here
        switch(bankRotationReg) {
          is(0.U) {
            registerBankB(io.xPosition) := io.dataIn
          }
          is(1.U) {
            registerBankC(io.xPosition) := io.dataIn
          }
          is(2.U) {
            registerBankA(io.xPosition) := io.dataIn
          }
        }
      }
      is(2.U) { //Downward
        switch(bankRotationReg) {
          is(0.U) {
            registerBankC(io.xPosition) := io.dataIn
          }
          is(1.U) {
            registerBankA(io.xPosition) := io.dataIn
          }
          is(2.U) {
            registerBankB(io.xPosition) := io.dataIn
          }
        }
      }
      is(3.U) { //Backwards
        switch(bankRotationReg) {
          is(0.U) {
            registerBankB(io.xPosition - 1.U) := io.dataIn
          }
          is(1.U) {
            registerBankC(io.xPosition - 1.U) := io.dataIn
          }
          is(2.U) {
            registerBankA(io.xPosition - 1.U) := io.dataIn
          }
        }
      }
      is(4.U) { //Upward
        switch(bankRotationReg) {
          is(0.U) {
            registerBankA(io.xPosition) := io.dataIn
          }
          is(1.U) {
            registerBankB(io.xPosition) := io.dataIn
          }
          is(2.U) {
            registerBankC(io.xPosition) := io.dataIn
          }
        }
      }
      is(5.U) { //forwardTwo
        switch(bankRotationReg) {
          is(0.U) {
            registerBankB(io.xPosition + 2.U) := io.dataIn
          }
          is(1.U) {
            registerBankC(io.xPosition + 2.U) := io.dataIn
          }
          is(2.U) {
            registerBankA(io.xPosition + 2.U) := io.dataIn
          }
        }
      }
    }
  }

  //Clears the appropriate registerbank
  when(io.shiftBanks) {
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