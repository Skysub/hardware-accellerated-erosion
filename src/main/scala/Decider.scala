import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

class Decider extends Module {
  val io = IO(new Bundle {
    val forward = Input(UInt(32.W))
    val here = Input(UInt(32.W))
    val downward = Input(UInt(32.W))
    val backward = Input(UInt(32.W))
    val upward = Input(UInt(32.W))
    val yPos = Input(UInt(16.W))

    val nextState = Output(UInt(4.W))
  })

  //Default value
  io.nextState := 0.U //Write white here

  //The decider
  //Checks for unknown values
  when(io.upward === 1.U) {
    io.nextState := 8.U //Read the upward value
  }
  when(io.backward === 1.U) {
    io.nextState := 7.U //Read the backward value
  }
  when(io.downward === 1.U) {
    io.nextState := 6.U //Read the downward value
  }
  when(io.here === 1.U) {
    io.nextState := 5.U //Read value here
  }
  when(io.forward === 1.U) {
    io.nextState := 4.U //Read the forward value
  }

  //Checks for any black pixels
  when(io.upward === 0.U) {
    io.nextState := 3.U //Write here black
  }
  when(io.backward === 0.U) {
    io.nextState := 3.U //Write here black
  }
  when(io.downward === 0.U) {
    io.nextState := 3.U //Write here black
  }
  when(io.here === 0.U) {
    when(io.yPos === 18.U){
      io.nextState := 3.U //Write here black
    } .otherwise {
      io.nextState := 2.U //Write forward one
    }
  }
  when(io.forward === 0.U) {
    when(io.yPos === 18.U) {
      io.nextState := 3.U //Write here black
    }.otherwise {
      io.nextState := 1.U //Write forward two
    }
  }
}