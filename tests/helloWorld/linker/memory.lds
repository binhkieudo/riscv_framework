MEMORY
{
  bootrom_mem (rx) : ORIGIN = 0x4000, LENGTH = 0x2000
  imem (rx) : ORIGIN = 0x300000, LENGTH = 0x1000
  dmem (rwx) : ORIGIN = 0x400000, LENGTH = 0x1000
}
