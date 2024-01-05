OUTPUT_ARCH("riscv")
ENTRY(_prog_start)

INCLUDE memory.lds

PHDRS
{
  text PT_LOAD;
  data PT_LOAD;
  bss PT_LOAD;
}

SECTIONS
{

  .text ALIGN((ORIGIN(imem) + 0x0), 8) : AT(ALIGN((ORIGIN(imem) + 0x0), 8)) {
    PROVIDE(_ftext = .);
    *(.text.init)
    *(.text.unlikely .text.unlikely.*)
    *(.text .text.* .gnu.linkonce.t.*)
    PROVIDE(_etext = .);
    . += 0x40; /* to create a gap between .text and .data b/c ifetch can fetch ahead from .data */
  } >imem  :text

  .eh_frame ALIGN((ADDR(.text) + SIZEOF(.text)), 8) : AT(ALIGN((LOADADDR(.text) + SIZEOF(.text)), 8)) {
    *(.eh_frame)
  } >imem  :text

  .rodata ALIGN((ORIGIN(imem) + 0x0), 8) : AT(ALIGN((ORIGIN(imem) + 0x0), 8)) ALIGN_WITH_INPUT {
    *(.rodata .rodata.* .gnu.linkonce.r.*)
  } >dmem  :data

  .srodata ALIGN((ADDR(.rodata) + SIZEOF(.rodata)), 8) : AT(ALIGN((LOADADDR(.rodata) + SIZEOF(.rodata)), 8)) ALIGN_WITH_INPUT {
    *(.srodata.cst16)
    *(.srodata.cst8)
    *(.srodata.cst4)
    *(.srodata.cst2)
    *(.srodata.*)
  } >dmem  :data

  .data ALIGN((ADDR(.srodata) + SIZEOF(.srodata)), 8) : AT(ALIGN((LOADADDR(.srodata) + SIZEOF(.srodata)), 8)) ALIGN_WITH_INPUT {
    *(.data .data.* .gnu.linkonce.d.*)
    *(.tohost) /* TODO: Support sections that aren't explicitly listed in this linker script */
  } >dmem  :data

  .sdata ALIGN((ADDR(.data) + SIZEOF(.data)), 8) : AT(ALIGN((LOADADDR(.data) + SIZEOF(.data)), 8)) ALIGN_WITH_INPUT {
    *(.sdata .sdata.* .gnu.linkonce.s.*)
  } >dmem  :data

  PROVIDE(_data = ADDR(.rodata));
  PROVIDE(_data_lma = LOADADDR(.rodata));
  PROVIDE(_edata = .);

  .bss ALIGN((ADDR(.sdata) + SIZEOF(.sdata)), 8) : AT(ALIGN((LOADADDR(.sdata) + SIZEOF(.sdata)), 8)) ALIGN(8) {
    PROVIDE(_fbss = .);
    PROVIDE(__global_pointer$ = . + 0x7C0);
    *(.sbss .sbss.* .gnu.linkonce.sb.*)
    *(.bss .bss.* .gnu.linkonce.b.*)
    . = ALIGN(8);
    PROVIDE(_ebss = .);
  } >dmem  :bss

  PROVIDE(_end = .);

  /*
   * heap_stack_region_usable_end: (ORIGIN(dmem) + LENGTH(dmem))
   * heap_stack_min_size: 4096
   * heap_stack_max_size: 1048576
   */
  PROVIDE(_sp = ALIGN(MIN((ORIGIN(dmem) + LENGTH(dmem)), _ebss + 1048576) - 7, 8));
  PROVIDE(_heap_end = _sp - 2048);

  /* This section is a noop and is only used for the ASSERT */
  .stack : {
    ASSERT(_sp >= (_ebss + 4096), "Error: No room left for the heap and stack");
  }
}
