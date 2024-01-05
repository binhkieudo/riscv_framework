# ------------------------- Base Clocks --------------------
create_clock -name sys_clock -period 10.0 [get_ports {sys_clock}]
set_input_jitter sys_clock 0.5
create_clock -name JTCK -period 100.0 [get_ports {jtag_jtag_TCK}]
set_input_jitter JTCK 0.5
# ------------------------- Clock Groups -------------------
set_clock_groups -asynchronous \
  -group [list [get_clocks { \
      clk_pll_i \
    }] [get_clocks -of_objects [get_pins { \
      mig/island/blackbox/ui_clk \
    }]]] \
  -group [list [get_clocks -of_objects [get_pins { \
      harnessSysPLLNode/clk_out2 \
      harnessSysPLLNode/clk_out3 \
    }]]] \
  -group [list [get_clocks -of_objects [get_pins { \
      harnessSysPLLNode/clk_out1 \
    }]]] \
  -group [list [get_clocks { \
      JTCK \
    }]]
# ------------------------- False Paths --------------------
set_false_path -through [get_pins {fpga_power_on/power_on_reset}]
# ------------------------- IO Timings ---------------------

