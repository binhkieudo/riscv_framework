// Generated by CIRCT unknown git version
// Standard header to adapt well known macros to our needs.
`ifndef RANDOMIZE
  `ifdef RANDOMIZE_REG_INIT
    `define RANDOMIZE
  `endif // RANDOMIZE_REG_INIT
`endif // not def RANDOMIZE
`ifndef RANDOMIZE
  `ifdef RANDOMIZE_MEM_INIT
    `define RANDOMIZE
  `endif // RANDOMIZE_MEM_INIT
`endif // not def RANDOMIZE

// RANDOM may be set to an expression that produces a 32-bit random unsigned value.
`ifndef RANDOM
  `define RANDOM $random
`endif // not def RANDOM

// Users can define 'PRINTF_COND' to add an extra gate to prints.
`ifndef PRINTF_COND_
  `ifdef PRINTF_COND
    `define PRINTF_COND_ (`PRINTF_COND)
  `else  // PRINTF_COND
    `define PRINTF_COND_ 1
  `endif // PRINTF_COND
`endif // not def PRINTF_COND_

// Users can define 'ASSERT_VERBOSE_COND' to add an extra gate to assert error printing.
`ifndef ASSERT_VERBOSE_COND_
  `ifdef ASSERT_VERBOSE_COND
    `define ASSERT_VERBOSE_COND_ (`ASSERT_VERBOSE_COND)
  `else  // ASSERT_VERBOSE_COND
    `define ASSERT_VERBOSE_COND_ 1
  `endif // ASSERT_VERBOSE_COND
`endif // not def ASSERT_VERBOSE_COND_

// Users can define 'STOP_COND' to add an extra gate to stop conditions.
`ifndef STOP_COND_
  `ifdef STOP_COND
    `define STOP_COND_ (`STOP_COND)
  `else  // STOP_COND
    `define STOP_COND_ 1
  `endif // STOP_COND
`endif // not def STOP_COND_

// Users can define INIT_RANDOM as general code that gets injected into the
// initializer block for modules with registers.
`ifndef INIT_RANDOM
  `define INIT_RANDOM
`endif // not def INIT_RANDOM

// If using random initialization, you can also define RANDOMIZE_DELAY to
// customize the delay used, otherwise 0.002 is used.
`ifndef RANDOMIZE_DELAY
  `define RANDOMIZE_DELAY 0.002
`endif // not def RANDOMIZE_DELAY

// Define INIT_RANDOM_PROLOG_ for use in our modules below.
`ifndef INIT_RANDOM_PROLOG_
  `ifdef RANDOMIZE
    `ifdef VERILATOR
      `define INIT_RANDOM_PROLOG_ `INIT_RANDOM
    `else  // VERILATOR
      `define INIT_RANDOM_PROLOG_ `INIT_RANDOM #`RANDOMIZE_DELAY begin end
    `endif // VERILATOR
  `else  // RANDOMIZE
    `define INIT_RANDOM_PROLOG_
  `endif // RANDOMIZE
`endif // not def INIT_RANDOM_PROLOG_

module AsyncQueueSink(
  input         clock,
                reset,
                io_deq_ready,
  input  [3:0]  io_async_mem_0_id,
  input  [31:0] io_async_mem_0_addr,
  input  [7:0]  io_async_mem_0_len,
  input  [2:0]  io_async_mem_0_size,
  input  [1:0]  io_async_mem_0_burst,
  input         io_async_mem_0_lock,
  input  [3:0]  io_async_mem_0_cache,
  input  [2:0]  io_async_mem_0_prot,
  input  [3:0]  io_async_mem_0_qos,
                io_async_mem_1_id,
  input  [31:0] io_async_mem_1_addr,
  input  [7:0]  io_async_mem_1_len,
  input  [2:0]  io_async_mem_1_size,
  input  [1:0]  io_async_mem_1_burst,
  input         io_async_mem_1_lock,
  input  [3:0]  io_async_mem_1_cache,
  input  [2:0]  io_async_mem_1_prot,
  input  [3:0]  io_async_mem_1_qos,
                io_async_mem_2_id,
  input  [31:0] io_async_mem_2_addr,
  input  [7:0]  io_async_mem_2_len,
  input  [2:0]  io_async_mem_2_size,
  input  [1:0]  io_async_mem_2_burst,
  input         io_async_mem_2_lock,
  input  [3:0]  io_async_mem_2_cache,
  input  [2:0]  io_async_mem_2_prot,
  input  [3:0]  io_async_mem_2_qos,
                io_async_mem_3_id,
  input  [31:0] io_async_mem_3_addr,
  input  [7:0]  io_async_mem_3_len,
  input  [2:0]  io_async_mem_3_size,
  input  [1:0]  io_async_mem_3_burst,
  input         io_async_mem_3_lock,
  input  [3:0]  io_async_mem_3_cache,
  input  [2:0]  io_async_mem_3_prot,
  input  [3:0]  io_async_mem_3_qos,
                io_async_mem_4_id,
  input  [31:0] io_async_mem_4_addr,
  input  [7:0]  io_async_mem_4_len,
  input  [2:0]  io_async_mem_4_size,
  input  [1:0]  io_async_mem_4_burst,
  input         io_async_mem_4_lock,
  input  [3:0]  io_async_mem_4_cache,
  input  [2:0]  io_async_mem_4_prot,
  input  [3:0]  io_async_mem_4_qos,
                io_async_mem_5_id,
  input  [31:0] io_async_mem_5_addr,
  input  [7:0]  io_async_mem_5_len,
  input  [2:0]  io_async_mem_5_size,
  input  [1:0]  io_async_mem_5_burst,
  input         io_async_mem_5_lock,
  input  [3:0]  io_async_mem_5_cache,
  input  [2:0]  io_async_mem_5_prot,
  input  [3:0]  io_async_mem_5_qos,
                io_async_mem_6_id,
  input  [31:0] io_async_mem_6_addr,
  input  [7:0]  io_async_mem_6_len,
  input  [2:0]  io_async_mem_6_size,
  input  [1:0]  io_async_mem_6_burst,
  input         io_async_mem_6_lock,
  input  [3:0]  io_async_mem_6_cache,
  input  [2:0]  io_async_mem_6_prot,
  input  [3:0]  io_async_mem_6_qos,
                io_async_mem_7_id,
  input  [31:0] io_async_mem_7_addr,
  input  [7:0]  io_async_mem_7_len,
  input  [2:0]  io_async_mem_7_size,
  input  [1:0]  io_async_mem_7_burst,
  input         io_async_mem_7_lock,
  input  [3:0]  io_async_mem_7_cache,
  input  [2:0]  io_async_mem_7_prot,
  input  [3:0]  io_async_mem_7_qos,
                io_async_widx,
  input         io_async_safe_widx_valid,
                io_async_safe_source_reset_n,
  output        io_deq_valid,
  output [3:0]  io_deq_bits_id,
  output [31:0] io_deq_bits_addr,
  output [7:0]  io_deq_bits_len,
  output [2:0]  io_deq_bits_size,
  output [1:0]  io_deq_bits_burst,
  output        io_deq_bits_lock,
  output [2:0]  io_deq_bits_prot,
  output [3:0]  io_deq_bits_qos,
                io_async_ridx,
  output        io_async_safe_ridx_valid,
                io_async_safe_sink_reset_n
);

  wire             _io_deq_valid_output;	// @[AsyncQueue.scala:162:29]
  wire             _source_valid_io_out;	// @[AsyncQueue.scala:172:31]
  wire             _source_extend_io_out;	// @[AsyncQueue.scala:171:31]
  wire             _sink_valid_0_io_out;	// @[AsyncQueue.scala:168:33]
  wire [60:0]      _io_deq_bits_deq_bits_reg_io_q;	// @[SynchronizerReg.scala:207:25]
  wire [3:0]       _widx_widx_gray_io_q;	// @[ShiftReg.scala:45:23]
  reg  [3:0]       ridx_ridx_bin;	// @[AsyncQueue.scala:52:25]
  wire [3:0]       _ridx_incremented_T_1 = ridx_ridx_bin + {3'h0, io_deq_ready & _io_deq_valid_output};	// @[AsyncQueue.scala:52:25, :53:43, :162:29, Decoupled.scala:51:35]
  wire [3:0]       ridx_incremented = _source_valid_io_out ? _ridx_incremented_T_1 : 4'h0;	// @[AsyncQueue.scala:53:{23,43}, :172:31]
  wire [3:0]       ridx = ridx_incremented ^ {1'h0, ridx_incremented[3:1]};	// @[AsyncQueue.scala:53:23, :54:{17,32}, :144:79]
  wire             valid = _source_valid_io_out & ridx != _widx_widx_gray_io_q;	// @[AsyncQueue.scala:54:17, :146:{28,36}, :172:31, ShiftReg.scala:45:23]
  wire [2:0]       index = ridx[2:0] ^ {ridx[3], 2'h0};	// @[AsyncQueue.scala:54:17, :152:{43,55,62,75}]
  wire [7:0][3:0]  _GEN = {{io_async_mem_7_id}, {io_async_mem_6_id}, {io_async_mem_5_id}, {io_async_mem_4_id}, {io_async_mem_3_id}, {io_async_mem_2_id}, {io_async_mem_1_id}, {io_async_mem_0_id}};	// @[SynchronizerReg.scala:209:24]
  wire [7:0][31:0] _GEN_0 = {{io_async_mem_7_addr}, {io_async_mem_6_addr}, {io_async_mem_5_addr}, {io_async_mem_4_addr}, {io_async_mem_3_addr}, {io_async_mem_2_addr}, {io_async_mem_1_addr}, {io_async_mem_0_addr}};	// @[SynchronizerReg.scala:209:24]
  wire [7:0][7:0]  _GEN_1 = {{io_async_mem_7_len}, {io_async_mem_6_len}, {io_async_mem_5_len}, {io_async_mem_4_len}, {io_async_mem_3_len}, {io_async_mem_2_len}, {io_async_mem_1_len}, {io_async_mem_0_len}};	// @[SynchronizerReg.scala:209:24]
  wire [7:0][2:0]  _GEN_2 = {{io_async_mem_7_size}, {io_async_mem_6_size}, {io_async_mem_5_size}, {io_async_mem_4_size}, {io_async_mem_3_size}, {io_async_mem_2_size}, {io_async_mem_1_size}, {io_async_mem_0_size}};	// @[SynchronizerReg.scala:209:24]
  wire [7:0][1:0]  _GEN_3 = {{io_async_mem_7_burst}, {io_async_mem_6_burst}, {io_async_mem_5_burst}, {io_async_mem_4_burst}, {io_async_mem_3_burst}, {io_async_mem_2_burst}, {io_async_mem_1_burst}, {io_async_mem_0_burst}};	// @[SynchronizerReg.scala:209:24]
  wire [7:0]       _GEN_4 = {{io_async_mem_7_lock}, {io_async_mem_6_lock}, {io_async_mem_5_lock}, {io_async_mem_4_lock}, {io_async_mem_3_lock}, {io_async_mem_2_lock}, {io_async_mem_1_lock}, {io_async_mem_0_lock}};	// @[SynchronizerReg.scala:209:24]
  wire [7:0][3:0]  _GEN_5 = {{io_async_mem_7_cache}, {io_async_mem_6_cache}, {io_async_mem_5_cache}, {io_async_mem_4_cache}, {io_async_mem_3_cache}, {io_async_mem_2_cache}, {io_async_mem_1_cache}, {io_async_mem_0_cache}};	// @[SynchronizerReg.scala:209:24]
  wire [7:0][2:0]  _GEN_6 = {{io_async_mem_7_prot}, {io_async_mem_6_prot}, {io_async_mem_5_prot}, {io_async_mem_4_prot}, {io_async_mem_3_prot}, {io_async_mem_2_prot}, {io_async_mem_1_prot}, {io_async_mem_0_prot}};	// @[SynchronizerReg.scala:209:24]
  wire [7:0][3:0]  _GEN_7 = {{io_async_mem_7_qos}, {io_async_mem_6_qos}, {io_async_mem_5_qos}, {io_async_mem_4_qos}, {io_async_mem_3_qos}, {io_async_mem_2_qos}, {io_async_mem_1_qos}, {io_async_mem_0_qos}};	// @[SynchronizerReg.scala:209:24]
  reg              valid_reg;	// @[AsyncQueue.scala:161:56]
  assign _io_deq_valid_output = valid_reg & _source_valid_io_out;	// @[AsyncQueue.scala:161:56, :162:29, :172:31]
  reg  [3:0]       ridx_gray;	// @[AsyncQueue.scala:164:55]
  always @(posedge clock or posedge reset) begin
    if (reset) begin
      ridx_ridx_bin <= 4'h0;	// @[AsyncQueue.scala:52:25, :53:23]
      valid_reg <= 1'h0;	// @[AsyncQueue.scala:144:79, :161:56]
      ridx_gray <= 4'h0;	// @[AsyncQueue.scala:53:23, :164:55]
    end
    else begin
      if (_source_valid_io_out)	// @[AsyncQueue.scala:172:31]
        ridx_ridx_bin <= _ridx_incremented_T_1;	// @[AsyncQueue.scala:52:25, :53:43]
      else	// @[AsyncQueue.scala:172:31]
        ridx_ridx_bin <= 4'h0;	// @[AsyncQueue.scala:52:25, :53:23]
      valid_reg <= valid;	// @[AsyncQueue.scala:146:28, :161:56]
      ridx_gray <= ridx;	// @[AsyncQueue.scala:54:17, :164:55]
    end
  end // always @(posedge, posedge)
  `ifndef SYNTHESIS
    `ifdef FIRRTL_BEFORE_INITIAL
      `FIRRTL_BEFORE_INITIAL
    `endif // FIRRTL_BEFORE_INITIAL
    logic [31:0] _RANDOM_0;
    initial begin
      `ifdef INIT_RANDOM_PROLOG_
        `INIT_RANDOM_PROLOG_
      `endif // INIT_RANDOM_PROLOG_
      `ifdef RANDOMIZE_REG_INIT
        _RANDOM_0 = `RANDOM;
        ridx_ridx_bin = _RANDOM_0[3:0];	// @[AsyncQueue.scala:52:25]
        valid_reg = _RANDOM_0[4];	// @[AsyncQueue.scala:52:25, :161:56]
        ridx_gray = _RANDOM_0[8:5];	// @[AsyncQueue.scala:52:25, :164:55]
      `endif // RANDOMIZE_REG_INIT
      `ifdef RANDOMIZE
        if (reset) begin
          ridx_ridx_bin = 4'h0;	// @[AsyncQueue.scala:52:25, :53:23]
          valid_reg = 1'h0;	// @[AsyncQueue.scala:144:79, :161:56]
          ridx_gray = 4'h0;	// @[AsyncQueue.scala:53:23, :164:55]
        end
      `endif // RANDOMIZE
    end // initial
    `ifdef FIRRTL_AFTER_INITIAL
      `FIRRTL_AFTER_INITIAL
    `endif // FIRRTL_AFTER_INITIAL
  `endif // not def SYNTHESIS
  AsyncResetSynchronizerShiftReg_w4_d3_i0 widx_widx_gray (	// @[ShiftReg.scala:45:23]
    .clock (clock),
    .reset (reset),
    .io_d  (io_async_widx),
    .io_q  (_widx_widx_gray_io_q)
  );
  ClockCrossingReg_w61 io_deq_bits_deq_bits_reg (	// @[SynchronizerReg.scala:207:25]
    .clock (clock),
    .io_d  ({_GEN[index], _GEN_0[index], _GEN_1[index], _GEN_2[index], _GEN_3[index], _GEN_4[index], _GEN_5[index], _GEN_6[index], _GEN_7[index]}),	// @[AsyncQueue.scala:152:55, SynchronizerReg.scala:209:24]
    .io_en (valid),	// @[AsyncQueue.scala:146:28]
    .io_q  (_io_deq_bits_deq_bits_reg_io_q)
  );
  AsyncValidSync_Arty100THarness_UNIQUIFIED sink_valid_0 (	// @[AsyncQueue.scala:168:33]
    .io_in  (1'h1),	// @[AsyncQueue.scala:143:30]
    .clock  (clock),
    .reset  (reset | ~io_async_safe_source_reset_n),	// @[AsyncQueue.scala:173:{42,45}]
    .io_out (_sink_valid_0_io_out)
  );
  AsyncValidSync_Arty100THarness_UNIQUIFIED sink_valid_1 (	// @[AsyncQueue.scala:169:33]
    .io_in  (_sink_valid_0_io_out),	// @[AsyncQueue.scala:168:33]
    .clock  (clock),
    .reset  (reset | ~io_async_safe_source_reset_n),	// @[AsyncQueue.scala:173:45, :174:42]
    .io_out (io_async_safe_ridx_valid)
  );
  AsyncValidSync_Arty100THarness_UNIQUIFIED source_extend (	// @[AsyncQueue.scala:171:31]
    .io_in  (io_async_safe_widx_valid),
    .clock  (clock),
    .reset  (reset | ~io_async_safe_source_reset_n),	// @[AsyncQueue.scala:173:45, :175:42]
    .io_out (_source_extend_io_out)
  );
  AsyncValidSync_Arty100THarness_UNIQUIFIED source_valid (	// @[AsyncQueue.scala:172:31]
    .io_in  (_source_extend_io_out),	// @[AsyncQueue.scala:171:31]
    .clock  (clock),
    .reset  (reset),
    .io_out (_source_valid_io_out)
  );
  assign io_deq_valid = _io_deq_valid_output;	// @[AsyncQueue.scala:162:29]
  assign io_deq_bits_id = _io_deq_bits_deq_bits_reg_io_q[60:57];	// @[SynchronizerReg.scala:207:25, :211:26]
  assign io_deq_bits_addr = _io_deq_bits_deq_bits_reg_io_q[56:25];	// @[SynchronizerReg.scala:207:25, :211:26]
  assign io_deq_bits_len = _io_deq_bits_deq_bits_reg_io_q[24:17];	// @[SynchronizerReg.scala:207:25, :211:26]
  assign io_deq_bits_size = _io_deq_bits_deq_bits_reg_io_q[16:14];	// @[SynchronizerReg.scala:207:25, :211:26]
  assign io_deq_bits_burst = _io_deq_bits_deq_bits_reg_io_q[13:12];	// @[SynchronizerReg.scala:207:25, :211:26]
  assign io_deq_bits_lock = _io_deq_bits_deq_bits_reg_io_q[11];	// @[SynchronizerReg.scala:207:25, :211:26]
  assign io_deq_bits_prot = _io_deq_bits_deq_bits_reg_io_q[6:4];	// @[SynchronizerReg.scala:207:25, :211:26]
  assign io_deq_bits_qos = _io_deq_bits_deq_bits_reg_io_q[3:0];	// @[SynchronizerReg.scala:207:25, :211:26]
  assign io_async_ridx = ridx_gray;	// @[AsyncQueue.scala:164:55]
  assign io_async_safe_sink_reset_n = ~reset;	// @[AsyncQueue.scala:189:25]
endmodule
