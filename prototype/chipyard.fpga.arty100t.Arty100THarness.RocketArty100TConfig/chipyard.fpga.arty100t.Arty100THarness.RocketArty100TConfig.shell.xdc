set_property PACKAGE_PIN {E3} [get_ports {sys_clock}]
set_property IOSTANDARD {LVCMOS33} [get_ports {sys_clock}]
set_property PACKAGE_PIN {H5} [get_ports {gpio_gpio_0}]
set_property IOSTANDARD {LVCMOS33} [get_ports {gpio_gpio_0}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {gpio_gpio_0}]]]
set_property PACKAGE_PIN {J5} [get_ports {gpio_gpio_1}]
set_property IOSTANDARD {LVCMOS33} [get_ports {gpio_gpio_1}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {gpio_gpio_1}]]]
set_property PACKAGE_PIN {T9} [get_ports {gpio_gpio_2}]
set_property IOSTANDARD {LVCMOS33} [get_ports {gpio_gpio_2}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {gpio_gpio_2}]]]
set_property PACKAGE_PIN {T10} [get_ports {gpio_gpio_3}]
set_property IOSTANDARD {LVCMOS33} [get_ports {gpio_gpio_3}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {gpio_gpio_3}]]]
set_property PACKAGE_PIN {E1} [get_ports {gpio_gpio_4}]
set_property IOSTANDARD {LVCMOS33} [get_ports {gpio_gpio_4}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {gpio_gpio_4}]]]
set_property PACKAGE_PIN {F6} [get_ports {gpio_gpio_5}]
set_property IOSTANDARD {LVCMOS33} [get_ports {gpio_gpio_5}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {gpio_gpio_5}]]]
set_property PACKAGE_PIN {G6} [get_ports {gpio_gpio_6}]
set_property IOSTANDARD {LVCMOS33} [get_ports {gpio_gpio_6}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {gpio_gpio_6}]]]
set_property PACKAGE_PIN {G4} [get_ports {gpio_gpio_7}]
set_property IOSTANDARD {LVCMOS33} [get_ports {gpio_gpio_7}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {gpio_gpio_7}]]]
set_property PACKAGE_PIN {J4} [get_ports {gpio_gpio_8}]
set_property IOSTANDARD {LVCMOS33} [get_ports {gpio_gpio_8}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {gpio_gpio_8}]]]
set_property PACKAGE_PIN {G3} [get_ports {gpio_gpio_9}]
set_property IOSTANDARD {LVCMOS33} [get_ports {gpio_gpio_9}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {gpio_gpio_9}]]]
set_property PACKAGE_PIN {H4} [get_ports {gpio_gpio_10}]
set_property IOSTANDARD {LVCMOS33} [get_ports {gpio_gpio_10}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {gpio_gpio_10}]]]
set_property PACKAGE_PIN {J2} [get_ports {gpio_gpio_11}]
set_property IOSTANDARD {LVCMOS33} [get_ports {gpio_gpio_11}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {gpio_gpio_11}]]]
set_property PACKAGE_PIN {J3} [get_ports {gpio_gpio_12}]
set_property IOSTANDARD {LVCMOS33} [get_ports {gpio_gpio_12}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {gpio_gpio_12}]]]
set_property PACKAGE_PIN {K2} [get_ports {gpio_gpio_13}]
set_property IOSTANDARD {LVCMOS33} [get_ports {gpio_gpio_13}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {gpio_gpio_13}]]]
set_property PACKAGE_PIN {H6} [get_ports {gpio_gpio_14}]
set_property IOSTANDARD {LVCMOS33} [get_ports {gpio_gpio_14}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {gpio_gpio_14}]]]
set_property PACKAGE_PIN {K1} [get_ports {gpio_gpio_15}]
set_property IOSTANDARD {LVCMOS33} [get_ports {gpio_gpio_15}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {gpio_gpio_15}]]]
set_property CLOCK_DEDICATED_ROUTE {FALSE} [get_nets [get_ports {jtag_jtag_TCK}]]
set_property PACKAGE_PIN {F4} [get_ports {jtag_jtag_TCK}]
set_property IOSTANDARD {LVCMOS33} [get_ports {jtag_jtag_TCK}]
set_property PULLUP {TRUE} [get_ports {jtag_jtag_TCK}]
set_property PACKAGE_PIN {D3} [get_ports {jtag_jtag_TMS}]
set_property IOSTANDARD {LVCMOS33} [get_ports {jtag_jtag_TMS}]
set_property PULLUP {TRUE} [get_ports {jtag_jtag_TMS}]
set_property PACKAGE_PIN {D4} [get_ports {jtag_jtag_TDI}]
set_property IOSTANDARD {LVCMOS33} [get_ports {jtag_jtag_TDI}]
set_property PULLUP {TRUE} [get_ports {jtag_jtag_TDI}]
set_property PACKAGE_PIN {F3} [get_ports {jtag_jtag_TDO}]
set_property IOSTANDARD {LVCMOS33} [get_ports {jtag_jtag_TDO}]
set_property PULLUP {TRUE} [get_ports {jtag_jtag_TDO}]
set_property PACKAGE_PIN {E2} [get_ports {jtag_srst_n}]
set_property IOSTANDARD {LVCMOS33} [get_ports {jtag_srst_n}]
set_property PULLUP {TRUE} [get_ports {jtag_srst_n}]
set_property PACKAGE_PIN {A9} [get_ports {uart_rxd}]
set_property IOSTANDARD {LVCMOS33} [get_ports {uart_rxd}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {uart_rxd}]]]
set_property PACKAGE_PIN {D10} [get_ports {uart_txd}]
set_property IOSTANDARD {LVCMOS33} [get_ports {uart_txd}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {uart_txd}]]]
set_property PACKAGE_PIN {C15} [get_ports {sdcard_spi_clk}]
set_property IOSTANDARD {LVCMOS33} [get_ports {sdcard_spi_clk}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {sdcard_spi_clk}]]]
set_property PACKAGE_PIN {E16} [get_ports {sdcard_spi_cs}]
set_property IOSTANDARD {LVCMOS33} [get_ports {sdcard_spi_cs}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {sdcard_spi_cs}]]]
set_property PACKAGE_PIN {D15} [get_ports {sdcard_spi_dat_0}]
set_property IOSTANDARD {LVCMOS33} [get_ports {sdcard_spi_dat_0}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {sdcard_spi_dat_0}]]]
set_property PACKAGE_PIN {J17} [get_ports {sdcard_spi_dat_1}]
set_property IOSTANDARD {LVCMOS33} [get_ports {sdcard_spi_dat_1}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {sdcard_spi_dat_1}]]]
set_property PACKAGE_PIN {J18} [get_ports {sdcard_spi_dat_2}]
set_property IOSTANDARD {LVCMOS33} [get_ports {sdcard_spi_dat_2}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {sdcard_spi_dat_2}]]]
set_property PACKAGE_PIN {E15} [get_ports {sdcard_spi_dat_3}]
set_property IOSTANDARD {LVCMOS33} [get_ports {sdcard_spi_dat_3}]
set_property IOB {TRUE} [ get_cells -of_objects [ all_fanin -flat -startpoints_only [get_ports {sdcard_spi_dat_3}]]]
set_property PULLUP {TRUE} [get_ports {sdcard_spi_cs}]
set_property PULLUP {TRUE} [get_ports {sdcard_spi_dat_0}]
set_property PULLUP {TRUE} [get_ports {sdcard_spi_dat_1}]
set_property PULLUP {TRUE} [get_ports {sdcard_spi_dat_2}]
set_property PULLUP {TRUE} [get_ports {sdcard_spi_dat_3}]
set_property BOARD_PIN {reset} [get_ports {reset}]