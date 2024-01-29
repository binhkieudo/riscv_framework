// See LICENSE.Sifive for license details.
#include <platform.h>
// #include <smp.h>
#include "common.h"
#include "include/platform.h"
#include "include/devices/clint.h"

// The hart that non-SMP tests should run on
#ifndef NONSMP_HART
#define NONSMP_HART 0
#endif

  .section .text.init
  .option norvc
  .globl _prog_start
_prog_start:
  csrr a0, mhartid
  li s1, 0
  beq a0, s1, _hart0
  li s1, 1  
  beq a0, s1, _hart1
  li s1, 2
  beq a0, s1, _hart2
  li s1, 3
  beq a0, s1, _hart3  
_hart0:
  li s1, 0
  li t0, MUTEX_KEY
  amoswap.w t1, s1, (t0)
  j _exit
_hart0_loop:
  add s0, zero, sp
  li sp, (CORE0_PRIVATE_MEM + 0xFF0)
  addi sp, sp, -16
  sd ra, 8(sp)
  sd s0, 0(sp)
  csrr a0, mhartid
  call main
  ld s0, 0(sp)
  ld ra, 8(sp)
  addi sp, sp, 16
  add sp, zero, s0
  ret
_hart1:
  wfi
  csrw mie, zero
  li s1, 0
  li t0, (CLINT_CTRL_ADDR + CLINT_MSIP1)
  amoswap.w t1, s1, (t0)
  li sp, (CORE1_PRIVATE_MEM + 0xFF0)
  csrr a0, mhartid
  call main
  li s1, 0x8
  csrw mie, s1  
  j  _hart1
_hart2:
  wfi
  csrw mie, zero
  li s1, 0
  li t0, (CLINT_CTRL_ADDR + CLINT_MSIP2)
  amoswap.w t1, s1, (t0)
  li sp, (CORE2_PRIVATE_MEM + 0xFF0)
  csrr a0, mhartid
  call main
  li s1, 0x8
  csrw mie, s1  
  j  _hart2
_hart3:
  wfi
  csrw mie, zero
  li s1, 0
  li t0, (CLINT_CTRL_ADDR + CLINT_MSIP3)
  amoswap.w t1, s1, (t0)
  li sp, (CORE3_PRIVATE_MEM + 0xFF0)
  csrr a0, mhartid
  call main
  li s1, 0x8
  csrw mie, s1  
  j  _hart3
_exit:
  li s1, 0x8
  csrw mie, s1
  csrr a0, mhartid
  la s0, _hart0_loop
  la s1, hart0_sym
  sd s0, 0(s1)
  ld s0, 0(s1)
  la a1, dtb // dtb address for next level bootloader
  li s1, PAYLOAD_DEST
  jr s1
_trap:
  j _trap


  .section .dtb
  .align 8
dtb:
  hart0_sym: .8byte 0x0
