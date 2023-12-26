MEMORY
{
  bootrom_mem (rx) : ORIGIN = 0x4000, LENGTH = 0x2000
  memory_mem (rwx) : ORIGIN = 0x40000000, LENGTH = 0x04000000
}
