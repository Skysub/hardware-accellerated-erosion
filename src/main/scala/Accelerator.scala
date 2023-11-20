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
  val idle :: setCorner :: outerLoop :: setEdge :: innerLoop :: checkPixel :: checkAdjacent :: setPixelBlack :: setPixelWhite :: done :: Nil = Enum(10)
  val stateReg = RegInit(idle)

  //Support registers
  val i = RegInit(0.U(32.W))
  val j = RegInit(0.U(32.W))
  val l = RegInit(1.U(32.W))

  //Default values
  io.done := false.B
  io.address := 0.U(16.W)
  io.dataWrite := 0.U(32.W)
  io.writeEnable := false.B


  //FSMD switch
  switch(stateReg) {
    is(idle) {
      when(io.start) {
        stateReg := setCorner
      }
    }

    is(setCorner) {

      switch(i) {
        is(0.U) {
          io.writeEnable := true.B
          io.address := 400.U
          io.dataWrite := 0.U(32.W)
          i := i + 1.U
        }
        is(1.U) {
          io.writeEnable := true.B
          io.address := 419.U
          io.dataWrite := 0.U(32.W)
          i := i + 1.U
        }
        is(2.U) {
          io.writeEnable := true.B
          io.address := 780.U
          io.dataWrite := 0.U(32.W)
          i := i + 1.U
        }
        is(3.U) {
          io.writeEnable := true.B
          io.address := 799.U
          io.dataWrite := 0.U(32.W)
          stateReg := outerLoop
        }
      }
    }

    is(outerLoop) {
      j := j + 1.U
      when(19.U === j) {
        stateReg := done
      }.otherwise {
        i := 0.U
        stateReg := setEdge

      }
    }
    is(setEdge) {
      switch(i) {
        is(0.U) {
          //Bottom edge
          io.writeEnable := true.B
          io.address := 400.U + i + 20.U * 19.U
          io.dataWrite := 0.U(32.W)
          i := i + 1.U
        }
        is(1.U) {
          //Top edge
          io.writeEnable := true.B
          io.address := 400.U + i
          io.dataWrite := 0.U(32.W)
          i := i + 1.U
        }
        is(2.U) {
          //Left edge
          io.writeEnable := true.B
          io.address := 400.U + i * 20.U
          io.dataWrite := 0.U(32.W)
          i := i + 1.U
        }
        is(3.U) {
          //Right edge
          io.writeEnable := true.B
          io.address := 400.U + i * 20.U + 19.U
          io.dataWrite := 0.U(32.W)
          stateReg := innerLoop
          l := 1.U
        }
      }
    }
    is(innerLoop) {
      l := l * 8.U
      when(l === 360.U) {
        stateReg := outerLoop
      }.otherwise {
        stateReg := checkPixel
      }
    }
    is(checkPixel) {
      io.address := 400.U + j + l
      when(io.dataRead === 0.U(32.W)) {
        stateReg := innerLoop
      }.otherwise {
        i := 0.U
        stateReg := checkAdjacent
      }
    }
    is(checkAdjacent) {
      switch(i) {
        is(0.U) {
          //Right pixel
          io.address := 400.U + j + l + 1.U
          when(io.dataRead === 0.U(32.W)) {
            stateReg := setPixelBlack
          }.otherwise {
            i := i + 1.U
          }
        }
        is(1.U) {
          //Left pixel
          io.address := 400.U + j + l - 1.U
          when(io.dataRead === 0.U(32.W)) {
            stateReg := setPixelBlack
          }.otherwise {
            i := i + 1.U
          }
        }
        is(2.U) {
          //Top pixel
          io.address := 400.U + j + l + 20.U
          when(io.dataRead === 0.U(32.W)) {
            stateReg := setPixelBlack
          }.otherwise {
            i := i + 1.U
          }
        }
        is(3.U) {
          //Bottom pixel
          io.address := 400.U + j + l - 20.U
          when(io.dataRead === 255.U(32.W)) {
            stateReg := setPixelBlack
          }.otherwise {
            stateReg := setPixelWhite
          }
        }
      }
    }
    is(setPixelBlack) {
      io.address := 400.U + j + l
      io.writeEnable := true.B
      io.dataWrite := 0.U(32.W)
      stateReg := innerLoop
    }

    is(setPixelWhite) {
      io.address := 400.U + j + l
      io.writeEnable := true.B
      io.dataWrite := 255.U(32.W)
      stateReg := innerLoop
    }

    is(done) {
      io.done := true.B
    }
  }
}
