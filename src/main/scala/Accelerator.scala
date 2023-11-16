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
  val here :: foward :: backward :: upward :: downward :: Nil = Enum(5) //read substates
  val edge :: writeHere :: fowardOne :: fowardTwo :: Nil = Enum(4) //write substates
  val mainStateReg = RegInit(idle)
  val readStateReg = RegInit(foward)
  val writeStateReg = RegInit(edge)

  //Registers
  val edgeReg = RegInit(0.U(16.W))
  val edgeStateReg = RegInit(edgeTop)

  //Default values
  io.writeEnable := false.B
  io.address := 0.U(16.W)
  io.done := false.B

  //Other stuff
  val edgeTop :: edgeBottom :: edgeLeft :: edgeRight :: Nil = Enum(4)

  //FSMD switch
  switch(mainStateReg) {
    is(idle) {
      when(io.start) {
        mainStateReg := write
        writeStateReg := edge
      }
    }

    is(read) {
      io.address := addressReg
      dataReg := Cat(0.U(24.W), ~io.dataRead(7, 0))
      mainStateReg := write
    }

    is(write) {
      io.writeEnable := true.B

      switch(writeStateReg){
        is(edge) {
          switch(edgeReg){

          }

          when(edgeReg === 799.U){
            mainStateReg := foward
          }
        }
        is(here){

        }
        is(fowardOne) {

        }
        is(fowardTwo) {

        }
      }

    }

    is(done) {
      io.done := true.B
      mainStateReg := done
    }
  }
}
