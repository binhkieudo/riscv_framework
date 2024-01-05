// Copyright 1986-2022 Xilinx, Inc. All Rights Reserved.
// --------------------------------------------------------------------------------
// Tool Version: Vivado v.2022.2 (lin64) Build 3671981 Fri Oct 14 04:59:54 MDT 2022
// Date        : Fri Jan  5 01:02:52 2024
// Host        : binhkieudo-ASUS running 64-bit Ubuntu 20.04.6 LTS
// Command     : write_verilog -force -mode synth_stub
//               /home/binhkieudo/Workspace/riscv_framework/fpga/generated-src/chipyard.fpga.arty100t.Arty100THarness.RocketArty100TConfig/Arty100THarness.gen/sources_1/ip/ila_0/ila_0_stub.v
// Design      : ila_0
// Purpose     : Stub declaration of top-level module interface
// Device      : xc7a100tcsg324-1
// --------------------------------------------------------------------------------

// This empty module with port declaration file causes synthesis tools to infer a black box for IP.
// The synthesis directives are for Synopsys Synplify support to prevent IO buffer insertion.
// Please paste the declaration into a Verilog source file or add the file as an additional source.
(* X_CORE_INFO = "ila,Vivado 2022.2" *)
module ila_0(clk, probe0, probe1, probe2, probe3)
/* synthesis syn_black_box black_box_pad_pin="clk,probe0[0:0],probe1[30:0],probe2[7:0],probe3[63:0]" */;
  input clk;
  input [0:0]probe0;
  input [30:0]probe1;
  input [7:0]probe2;
  input [63:0]probe3;
endmodule
