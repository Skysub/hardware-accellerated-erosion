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

  //Modules
  val registerFile = Module(new RegisterFile())

  //State enum and register
  val idle :: done :: write :: read :: Nil = Enum(4) //main states
  val here :: foward :: backward :: upward :: downward :: Nil = Enum(5) //read substates
  val edge :: writeHere :: fowardOne :: fowardTwo :: Nil = Enum(4) //write substates
  val edgeTop :: edgeBottom :: edgeLeft :: edgeRight :: Nil = Enum(4) //Edge subsubstates
  val mainStateReg = RegInit(idle)
  val readStateReg = RegInit(foward)
  val writeStateReg = RegInit(edge)
  val edgeStateReg = RegInit(edgeTop)

  //Registers
  val edgeReg = RegInit(400.U(16.W))

  //Default values
  io.writeEnable := false.B
  io.address := 0.U(16.W)
  io.done := false.B
  io.dataWrite := 1337.U(32.W)

  registerFile.io.xPosition := 1.U
  registerFile.io.shiftBanks := false.B
  registerFile.io.regWrite := false.B
  registerFile.io.dataIn := 1.U
  registerFile.io.writeWhere := 1.U

  //FSMD switch
  switch(mainStateReg) {
    is(idle) {
      when(io.start) {
        mainStateReg := write
        writeStateReg := edge
        edgeReg := 400.U(16.W)
      }
    }

    is(read) {
      //io.address := addressReg
      //dataReg := Cat(0.U(24.W), ~io.dataRead(7, 0))
      //mainStateReg := write

      switch(readStateReg){
        is(foward){

        }
        is(here){

        }
        is(downward){

        }
        is(backward){

        }
        is(upward){

        }
      }
    }

    is(write) {
      io.writeEnable := true.B

      switch(writeStateReg){




        is(writeHere){

        }
        is(fowardOne) {

        }
        is(fowardTwo) {

        }

        //The edge states
        is(edge) {
          io.address := edgeReg
          io.dataWrite := 1.U(32.W)
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
            readStateReg := foward
            mainStateReg := done //FINISH AFTER EDGE HAS BEEN WRITTEN (DEBUG)
          }
        } //Edge state done
      }
    }

    is(done) {
      io.done := true.B
      mainStateReg := done
    }
  }
}
