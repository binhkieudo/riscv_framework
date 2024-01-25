#include "platform.h"  
  
  .section .text.init
  .option norvc
  .globl mux_lock
  .globl mux_unlock
  .globl sem_wait
  .globl sem_signal
  .globl get_hartid
  .globl atomic_write
  .globl sem_wait
// ====== Lock ====================  
lock:
    li t0, 1
    li a0, BRAM_MEM_ADDR
_again:
    lw t1, 0(a0)   // Check if lock is held
    bnez t1, _again  // Retry if held
    amoswap.w t1, t0, (a0)
    bnez t1, _again  // Retry if held
    ret
// ====== Unlock ==================
unlock:
    li t0, 0
    li a0, BRAM_MEM_ADDR
    lw t1, 0(a0)   // Check if lock is held
    beqz t1, _exit_unlock
    amoswap.w t0, t0, (a0)
_exit_unlock:
    ret
// ====== Get hartid ==============
get_hartid:
    csrr a0, mhartid
    ret
// ====== Atomic write ============
atomic_write:
    amoswap.w t0, a1, (a0)
    ret
// ====== Semaphore wait ==========
sem_wait:
    amoswap.w t0, zero, (a0)
    li s0, 0x8
    csrw mie, s0
    wfi
    csrw mie, zero
    amoswap.w t0, zero, (a0)
    ret
// ====== Semaphore signal ========
sem_signal:
    li t0, 1
    amoswap.w t1, t0, (a0)
    ret