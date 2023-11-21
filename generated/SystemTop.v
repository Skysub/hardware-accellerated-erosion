module DataMemory(
  input         clock,
  input  [15:0] io_address,
  output [31:0] io_dataRead,
  input         io_writeEnable,
  input  [31:0] io_dataWrite,
  input         io_testerEnable,
  input  [15:0] io_testerAddress,
  output [31:0] io_testerDataRead,
  input         io_testerWriteEnable,
  input  [31:0] io_testerDataWrite
);
`ifdef RANDOMIZE_MEM_INIT
  reg [31:0] _RAND_0;
`endif // RANDOMIZE_MEM_INIT
  reg [31:0] memory [0:65535]; // @[DataMemory.scala 18:20]
  wire [31:0] memory__T_data; // @[DataMemory.scala 18:20]
  wire [15:0] memory__T_addr; // @[DataMemory.scala 18:20]
  wire [31:0] memory__T_2_data; // @[DataMemory.scala 18:20]
  wire [15:0] memory__T_2_addr; // @[DataMemory.scala 18:20]
  wire [31:0] memory__T_1_data; // @[DataMemory.scala 18:20]
  wire [15:0] memory__T_1_addr; // @[DataMemory.scala 18:20]
  wire  memory__T_1_mask; // @[DataMemory.scala 18:20]
  wire  memory__T_1_en; // @[DataMemory.scala 18:20]
  wire [31:0] memory__T_3_data; // @[DataMemory.scala 18:20]
  wire [15:0] memory__T_3_addr; // @[DataMemory.scala 18:20]
  wire  memory__T_3_mask; // @[DataMemory.scala 18:20]
  wire  memory__T_3_en; // @[DataMemory.scala 18:20]
  wire [31:0] _GEN_5 = io_testerWriteEnable ? io_testerDataWrite : memory__T_data; // @[DataMemory.scala 27:32]
  wire [31:0] _GEN_11 = io_writeEnable ? io_dataWrite : memory__T_2_data; // @[DataMemory.scala 37:26]
  assign memory__T_addr = io_testerAddress;
  assign memory__T_data = memory[memory__T_addr]; // @[DataMemory.scala 18:20]
  assign memory__T_2_addr = io_address;
  assign memory__T_2_data = memory[memory__T_2_addr]; // @[DataMemory.scala 18:20]
  assign memory__T_1_data = io_testerDataWrite;
  assign memory__T_1_addr = io_testerAddress;
  assign memory__T_1_mask = 1'h1;
  assign memory__T_1_en = io_testerEnable & io_testerWriteEnable;
  assign memory__T_3_data = io_dataWrite;
  assign memory__T_3_addr = io_address;
  assign memory__T_3_mask = 1'h1;
  assign memory__T_3_en = io_testerEnable ? 1'h0 : io_writeEnable;
  assign io_dataRead = io_testerEnable ? 32'h0 : _GEN_11; // @[DataMemory.scala 26:17 DataMemory.scala 34:17 DataMemory.scala 40:19]
  assign io_testerDataRead = io_testerEnable ? _GEN_5 : 32'h0; // @[DataMemory.scala 24:23 DataMemory.scala 30:25 DataMemory.scala 36:23]
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_MEM_INIT
  _RAND_0 = {1{`RANDOM}};
  for (initvar = 0; initvar < 65536; initvar = initvar+1)
    memory[initvar] = _RAND_0[31:0];
`endif // RANDOMIZE_MEM_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
  always @(posedge clock) begin
    if(memory__T_1_en & memory__T_1_mask) begin
      memory[memory__T_1_addr] <= memory__T_1_data; // @[DataMemory.scala 18:20]
    end
    if(memory__T_3_en & memory__T_3_mask) begin
      memory[memory__T_3_addr] <= memory__T_3_data; // @[DataMemory.scala 18:20]
    end
  end
endmodule
module Accelerator(
  input         clock,
  input         reset,
  input         io_start,
  output        io_done,
  output [15:0] io_address,
  input  [31:0] io_dataRead,
  output        io_writeEnable,
  output [31:0] io_dataWrite
);
`ifdef RANDOMIZE_REG_INIT
  reg [31:0] _RAND_0;
  reg [31:0] _RAND_1;
  reg [31:0] _RAND_2;
  reg [31:0] _RAND_3;
`endif // RANDOMIZE_REG_INIT
  reg [3:0] stateReg; // @[Accelerator.scala 18:25]
  reg [31:0] i; // @[Accelerator.scala 21:18]
  reg [31:0] j; // @[Accelerator.scala 22:18]
  reg [31:0] l; // @[Accelerator.scala 23:18]
  wire  _T = 4'h2 == stateReg; // @[Conditional.scala 37:30]
  wire  _T_1 = 4'h3 == stateReg; // @[Conditional.scala 37:30]
  wire  _T_2 = 32'h0 == i; // @[Conditional.scala 37:30]
  wire [31:0] _T_4 = i + 32'h1; // @[Accelerator.scala 47:18]
  wire  _T_5 = 32'h1 == i; // @[Conditional.scala 37:30]
  wire  _T_8 = 32'h2 == i; // @[Conditional.scala 37:30]
  wire  _T_11 = 32'h3 == i; // @[Conditional.scala 37:30]
  wire [15:0] _GEN_2 = _T_11 ? 16'h31f : 16'h0; // @[Conditional.scala 39:67]
  wire  _GEN_5 = _T_8 | _T_11; // @[Conditional.scala 39:67]
  wire [15:0] _GEN_6 = _T_8 ? 16'h30c : _GEN_2; // @[Conditional.scala 39:67]
  wire  _GEN_10 = _T_5 | _GEN_5; // @[Conditional.scala 39:67]
  wire [15:0] _GEN_11 = _T_5 ? 16'h1a3 : _GEN_6; // @[Conditional.scala 39:67]
  wire  _GEN_15 = _T_2 | _GEN_10; // @[Conditional.scala 40:58]
  wire [15:0] _GEN_16 = _T_2 ? 16'h190 : _GEN_11; // @[Conditional.scala 40:58]
  wire  _T_12 = 4'h4 == stateReg; // @[Conditional.scala 37:30]
  wire [31:0] _T_14 = j + 32'h1; // @[Accelerator.scala 71:14]
  wire  _T_15 = j == 32'h12; // @[Accelerator.scala 72:14]
  wire  _T_16 = 4'h5 == stateReg; // @[Conditional.scala 37:30]
  wire [31:0] _T_19 = 32'h30c + j; // @[Accelerator.scala 85:31]
  wire [31:0] _T_24 = 32'h190 + j; // @[Accelerator.scala 92:31]
  wire [36:0] _T_28 = j * 32'h14; // @[Accelerator.scala 99:35]
  wire [36:0] _T_30 = 37'h190 + _T_28; // @[Accelerator.scala 99:31]
  wire [36:0] _T_36 = 37'h1a3 + _T_28; // @[Accelerator.scala 106:31]
  wire [36:0] _GEN_23 = _T_11 ? _T_36 : 37'h0; // @[Conditional.scala 39:67]
  wire [36:0] _GEN_28 = _T_8 ? _T_30 : _GEN_23; // @[Conditional.scala 39:67]
  wire [36:0] _GEN_34 = _T_5 ? {{5'd0}, _T_24} : _GEN_28; // @[Conditional.scala 39:67]
  wire [36:0] _GEN_40 = _T_2 ? {{5'd0}, _T_19} : _GEN_34; // @[Conditional.scala 40:58]
  wire  _T_37 = 4'h0 == stateReg; // @[Conditional.scala 37:30]
  wire [31:0] _T_39 = l + 32'h14; // @[Accelerator.scala 114:14]
  wire  _T_40 = l == 32'h168; // @[Accelerator.scala 115:14]
  wire  _T_41 = 4'h1 == stateReg; // @[Conditional.scala 37:30]
  wire [31:0] _T_43 = j + l; // @[Accelerator.scala 122:23]
  wire  _T_44 = io_dataRead == 32'h0; // @[Accelerator.scala 123:24]
  wire  _T_45 = 4'h6 == stateReg; // @[Conditional.scala 37:30]
  wire [31:0] _T_50 = _T_43 + 32'h1; // @[Accelerator.scala 134:31]
  wire [31:0] _T_58 = _T_43 - 32'h1; // @[Accelerator.scala 143:31]
  wire [31:0] _T_66 = _T_43 + 32'h14; // @[Accelerator.scala 152:31]
  wire [31:0] _T_74 = _T_43 - 32'h14; // @[Accelerator.scala 161:31]
  wire [31:0] _GEN_55 = _T_11 ? _T_74 : 32'h0; // @[Conditional.scala 39:67]
  wire [31:0] _GEN_57 = _T_8 ? _T_66 : _GEN_55; // @[Conditional.scala 39:67]
  wire [31:0] _GEN_60 = _T_5 ? _T_58 : _GEN_57; // @[Conditional.scala 39:67]
  wire [31:0] _GEN_63 = _T_2 ? _T_50 : _GEN_60; // @[Conditional.scala 40:58]
  wire  _T_76 = 4'h7 == stateReg; // @[Conditional.scala 37:30]
  wire [31:0] _T_80 = _T_24 + l; // @[Accelerator.scala 172:31]
  wire  _T_81 = 4'h8 == stateReg; // @[Conditional.scala 37:30]
  wire  _T_86 = 4'h9 == stateReg; // @[Conditional.scala 37:30]
  wire [31:0] _GEN_67 = _T_81 ? _T_80 : 32'h0; // @[Conditional.scala 39:67]
  wire [31:0] _GEN_69 = _T_81 ? 32'hff : 32'h0; // @[Conditional.scala 39:67]
  wire  _GEN_71 = _T_81 ? 1'h0 : _T_86; // @[Conditional.scala 39:67]
  wire [31:0] _GEN_72 = _T_76 ? _T_80 : _GEN_67; // @[Conditional.scala 39:67]
  wire  _GEN_73 = _T_76 | _T_81; // @[Conditional.scala 39:67]
  wire [31:0] _GEN_74 = _T_76 ? 32'h0 : _GEN_69; // @[Conditional.scala 39:67]
  wire  _GEN_76 = _T_76 ? 1'h0 : _GEN_71; // @[Conditional.scala 39:67]
  wire [31:0] _GEN_77 = _T_45 ? _GEN_63 : _GEN_72; // @[Conditional.scala 39:67]
  wire  _GEN_80 = _T_45 ? 1'h0 : _GEN_73; // @[Conditional.scala 39:67]
  wire [31:0] _GEN_81 = _T_45 ? 32'h0 : _GEN_74; // @[Conditional.scala 39:67]
  wire  _GEN_82 = _T_45 ? 1'h0 : _GEN_76; // @[Conditional.scala 39:67]
  wire [31:0] _GEN_83 = _T_41 ? _T_43 : _GEN_77; // @[Conditional.scala 39:67]
  wire  _GEN_86 = _T_41 ? 1'h0 : _GEN_80; // @[Conditional.scala 39:67]
  wire [31:0] _GEN_87 = _T_41 ? 32'h0 : _GEN_81; // @[Conditional.scala 39:67]
  wire  _GEN_88 = _T_41 ? 1'h0 : _GEN_82; // @[Conditional.scala 39:67]
  wire [31:0] _GEN_91 = _T_37 ? 32'h0 : _GEN_83; // @[Conditional.scala 39:67]
  wire  _GEN_93 = _T_37 ? 1'h0 : _GEN_86; // @[Conditional.scala 39:67]
  wire [31:0] _GEN_94 = _T_37 ? 32'h0 : _GEN_87; // @[Conditional.scala 39:67]
  wire  _GEN_95 = _T_37 ? 1'h0 : _GEN_88; // @[Conditional.scala 39:67]
  wire  _GEN_96 = _T_16 ? _GEN_15 : _GEN_93; // @[Conditional.scala 39:67]
  wire [36:0] _GEN_97 = _T_16 ? _GEN_40 : {{5'd0}, _GEN_91}; // @[Conditional.scala 39:67]
  wire [31:0] _GEN_98 = _T_16 ? 32'h0 : _GEN_94; // @[Conditional.scala 39:67]
  wire  _GEN_102 = _T_16 ? 1'h0 : _GEN_95; // @[Conditional.scala 39:67]
  wire  _GEN_106 = _T_12 ? 1'h0 : _GEN_96; // @[Conditional.scala 39:67]
  wire [36:0] _GEN_107 = _T_12 ? 37'h0 : _GEN_97; // @[Conditional.scala 39:67]
  wire [31:0] _GEN_108 = _T_12 ? 32'h0 : _GEN_98; // @[Conditional.scala 39:67]
  wire  _GEN_110 = _T_12 ? 1'h0 : _GEN_102; // @[Conditional.scala 39:67]
  wire  _GEN_111 = _T_1 ? _GEN_15 : _GEN_106; // @[Conditional.scala 39:67]
  wire [36:0] _GEN_112 = _T_1 ? {{21'd0}, _GEN_16} : _GEN_107; // @[Conditional.scala 39:67]
  wire [31:0] _GEN_113 = _T_1 ? 32'h0 : _GEN_108; // @[Conditional.scala 39:67]
  wire  _GEN_118 = _T_1 ? 1'h0 : _GEN_110; // @[Conditional.scala 39:67]
  wire [36:0] _GEN_121 = _T ? 37'h0 : _GEN_112; // @[Conditional.scala 40:58]
  assign io_done = _T ? 1'h0 : _GEN_118; // @[Accelerator.scala 26:11 Accelerator.scala 186:15]
  assign io_address = _GEN_121[15:0]; // @[Accelerator.scala 27:14 Accelerator.scala 45:22 Accelerator.scala 51:22 Accelerator.scala 57:22 Accelerator.scala 63:22 Accelerator.scala 85:22 Accelerator.scala 92:22 Accelerator.scala 99:22 Accelerator.scala 106:22 Accelerator.scala 122:18 Accelerator.scala 134:22 Accelerator.scala 143:22 Accelerator.scala 152:22 Accelerator.scala 161:22 Accelerator.scala 172:18 Accelerator.scala 179:18]
  assign io_writeEnable = _T ? 1'h0 : _GEN_111; // @[Accelerator.scala 29:18 Accelerator.scala 44:26 Accelerator.scala 50:26 Accelerator.scala 56:26 Accelerator.scala 62:26 Accelerator.scala 84:26 Accelerator.scala 91:26 Accelerator.scala 98:26 Accelerator.scala 105:26 Accelerator.scala 173:22 Accelerator.scala 180:22]
  assign io_dataWrite = _T ? 32'h0 : _GEN_113; // @[Accelerator.scala 28:16 Accelerator.scala 46:24 Accelerator.scala 52:24 Accelerator.scala 58:24 Accelerator.scala 64:24 Accelerator.scala 86:24 Accelerator.scala 93:24 Accelerator.scala 100:24 Accelerator.scala 107:24 Accelerator.scala 174:20 Accelerator.scala 181:20]
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
`ifdef FIRRTL_BEFORE_INITIAL
`FIRRTL_BEFORE_INITIAL
`endif
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
`ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  stateReg = _RAND_0[3:0];
  _RAND_1 = {1{`RANDOM}};
  i = _RAND_1[31:0];
  _RAND_2 = {1{`RANDOM}};
  j = _RAND_2[31:0];
  _RAND_3 = {1{`RANDOM}};
  l = _RAND_3[31:0];
`endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`ifdef FIRRTL_AFTER_INITIAL
`FIRRTL_AFTER_INITIAL
`endif
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      stateReg <= 4'h2;
    end else if (_T) begin
      if (io_start) begin
        stateReg <= 4'h3;
      end
    end else if (_T_1) begin
      if (!(_T_2)) begin
        if (!(_T_5)) begin
          if (!(_T_8)) begin
            if (_T_11) begin
              stateReg <= 4'h4;
            end
          end
        end
      end
    end else if (_T_12) begin
      if (_T_15) begin
        stateReg <= 4'h9;
      end else begin
        stateReg <= 4'h5;
      end
    end else if (_T_16) begin
      if (!(_T_2)) begin
        if (!(_T_5)) begin
          if (!(_T_8)) begin
            if (_T_11) begin
              stateReg <= 4'h0;
            end
          end
        end
      end
    end else if (_T_37) begin
      if (_T_40) begin
        stateReg <= 4'h4;
      end else begin
        stateReg <= 4'h1;
      end
    end else if (_T_41) begin
      if (_T_44) begin
        stateReg <= 4'h0;
      end else begin
        stateReg <= 4'h6;
      end
    end else if (_T_45) begin
      if (_T_2) begin
        if (_T_44) begin
          stateReg <= 4'h7;
        end
      end else if (_T_5) begin
        if (_T_44) begin
          stateReg <= 4'h7;
        end
      end else if (_T_8) begin
        if (_T_44) begin
          stateReg <= 4'h7;
        end
      end else if (_T_11) begin
        if (_T_44) begin
          stateReg <= 4'h7;
        end else begin
          stateReg <= 4'h8;
        end
      end
    end else if (_T_76) begin
      stateReg <= 4'h0;
    end else if (_T_81) begin
      stateReg <= 4'h0;
    end
    if (reset) begin
      i <= 32'h0;
    end else if (!(_T)) begin
      if (_T_1) begin
        if (_T_2) begin
          i <= _T_4;
        end else if (_T_5) begin
          i <= _T_4;
        end else if (_T_8) begin
          i <= _T_4;
        end
      end else if (_T_12) begin
        if (!(_T_15)) begin
          i <= 32'h0;
        end
      end else if (_T_16) begin
        if (_T_2) begin
          i <= _T_4;
        end else if (_T_5) begin
          i <= _T_4;
        end else if (_T_8) begin
          i <= _T_4;
        end
      end else if (!(_T_37)) begin
        if (_T_41) begin
          if (!(_T_44)) begin
            i <= 32'h0;
          end
        end else if (_T_45) begin
          if (_T_2) begin
            if (!(_T_44)) begin
              i <= _T_4;
            end
          end else if (_T_5) begin
            if (!(_T_44)) begin
              i <= _T_4;
            end
          end else if (_T_8) begin
            if (!(_T_44)) begin
              i <= _T_4;
            end
          end
        end
      end
    end
    if (reset) begin
      j <= 32'h0;
    end else if (!(_T)) begin
      if (!(_T_1)) begin
        if (_T_12) begin
          j <= _T_14;
        end
      end
    end
    if (reset) begin
      l <= 32'h0;
    end else if (!(_T)) begin
      if (!(_T_1)) begin
        if (!(_T_12)) begin
          if (_T_16) begin
            if (!(_T_2)) begin
              if (!(_T_5)) begin
                if (!(_T_8)) begin
                  if (_T_11) begin
                    l <= 32'h0;
                  end
                end
              end
            end
          end else if (_T_37) begin
            l <= _T_39;
          end
        end
      end
    end
  end
endmodule
module SystemTop(
  input         clock,
  input         reset,
  output        io_done,
  input         io_start,
  input         io_testerDataMemEnable,
  input  [15:0] io_testerDataMemAddress,
  output [31:0] io_testerDataMemDataRead,
  input         io_testerDataMemWriteEnable,
  input  [31:0] io_testerDataMemDataWrite
);
  wire  dataMemory_clock; // @[SystemTop.scala 18:26]
  wire [15:0] dataMemory_io_address; // @[SystemTop.scala 18:26]
  wire [31:0] dataMemory_io_dataRead; // @[SystemTop.scala 18:26]
  wire  dataMemory_io_writeEnable; // @[SystemTop.scala 18:26]
  wire [31:0] dataMemory_io_dataWrite; // @[SystemTop.scala 18:26]
  wire  dataMemory_io_testerEnable; // @[SystemTop.scala 18:26]
  wire [15:0] dataMemory_io_testerAddress; // @[SystemTop.scala 18:26]
  wire [31:0] dataMemory_io_testerDataRead; // @[SystemTop.scala 18:26]
  wire  dataMemory_io_testerWriteEnable; // @[SystemTop.scala 18:26]
  wire [31:0] dataMemory_io_testerDataWrite; // @[SystemTop.scala 18:26]
  wire  accelerator_clock; // @[SystemTop.scala 19:27]
  wire  accelerator_reset; // @[SystemTop.scala 19:27]
  wire  accelerator_io_start; // @[SystemTop.scala 19:27]
  wire  accelerator_io_done; // @[SystemTop.scala 19:27]
  wire [15:0] accelerator_io_address; // @[SystemTop.scala 19:27]
  wire [31:0] accelerator_io_dataRead; // @[SystemTop.scala 19:27]
  wire  accelerator_io_writeEnable; // @[SystemTop.scala 19:27]
  wire [31:0] accelerator_io_dataWrite; // @[SystemTop.scala 19:27]
  DataMemory dataMemory ( // @[SystemTop.scala 18:26]
    .clock(dataMemory_clock),
    .io_address(dataMemory_io_address),
    .io_dataRead(dataMemory_io_dataRead),
    .io_writeEnable(dataMemory_io_writeEnable),
    .io_dataWrite(dataMemory_io_dataWrite),
    .io_testerEnable(dataMemory_io_testerEnable),
    .io_testerAddress(dataMemory_io_testerAddress),
    .io_testerDataRead(dataMemory_io_testerDataRead),
    .io_testerWriteEnable(dataMemory_io_testerWriteEnable),
    .io_testerDataWrite(dataMemory_io_testerDataWrite)
  );
  Accelerator accelerator ( // @[SystemTop.scala 19:27]
    .clock(accelerator_clock),
    .reset(accelerator_reset),
    .io_start(accelerator_io_start),
    .io_done(accelerator_io_done),
    .io_address(accelerator_io_address),
    .io_dataRead(accelerator_io_dataRead),
    .io_writeEnable(accelerator_io_writeEnable),
    .io_dataWrite(accelerator_io_dataWrite)
  );
  assign io_done = accelerator_io_done; // @[SystemTop.scala 23:11]
  assign io_testerDataMemDataRead = dataMemory_io_testerDataRead; // @[SystemTop.scala 34:28]
  assign dataMemory_clock = clock;
  assign dataMemory_io_address = accelerator_io_address; // @[SystemTop.scala 28:25]
  assign dataMemory_io_writeEnable = accelerator_io_writeEnable; // @[SystemTop.scala 30:29]
  assign dataMemory_io_dataWrite = accelerator_io_dataWrite; // @[SystemTop.scala 29:27]
  assign dataMemory_io_testerEnable = io_testerDataMemEnable; // @[SystemTop.scala 36:30]
  assign dataMemory_io_testerAddress = io_testerDataMemAddress; // @[SystemTop.scala 33:31]
  assign dataMemory_io_testerWriteEnable = io_testerDataMemWriteEnable; // @[SystemTop.scala 37:35]
  assign dataMemory_io_testerDataWrite = io_testerDataMemDataWrite; // @[SystemTop.scala 35:33]
  assign accelerator_clock = clock;
  assign accelerator_reset = reset;
  assign accelerator_io_start = io_start; // @[SystemTop.scala 24:24]
  assign accelerator_io_dataRead = dataMemory_io_dataRead; // @[SystemTop.scala 27:27]
endmodule
