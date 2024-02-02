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
  .globl randi
  .globl mux_lock_n
  .globl mux_unlock_n
// ====== Lock ====================  
mux_lock:
    li t0, 1
    li a0, MUTEX_KEY
_again:
    lw t1, 0(a0)   // Check if lock is held
    bnez t1, _again  // Retry if held
    amoswap.w t1, t0, (a0)
    bnez t1, _again  // Retry if held
    ret
// ====== Lock specific core =======  
mux_lock_n:
    csrr t0, mhartid
_hart0_lock_n:
    li t1, 0
    bne t0, t1, _hart1_lock_n
    li a0, C0_MUTEX_KEY
    j _mux_lock_n_lock
_hart1_lock_n:
    li t1, 1
    bne t0, t1, _hart2_lock_n
    li a0, C1_MUTEX_KEY
    j _mux_lock_n_lock
_hart2_lock_n:
    li t1, 2
    bne t0, t1, _hart3_lock_n
    li a0, C2_MUTEX_KEY
    j _mux_lock_n_lock
_hart3_lock_n:
    li a0, C3_MUTEX_KEY
_mux_lock_n_lock:
    li t0, 1
    amoswap.w t1, t0, (a0)
_mux_lock_n_loop: // Wait until other core unlock
    lw t0, 0(a0)
    bnez t0, _mux_lock_n_loop
    ret
// ====== Unlock ==================
mux_unlock:
    li t0, 0
    li a0, MUTEX_KEY
    lw t1, 0(a0)   // Check if lock is held
    beqz t1, _exit_unlock
    amoswap.w t0, t0, (a0)
_exit_unlock:
    ret
// ====== Unlock specific core ==== (Wait until the unlock core is locked then unlock)
mux_unlock_n:
    mv t0, a0
_hart0_unlock_n:
    li t1, 0
    bne t0, t1, _hart1_unlock_n
    li a0, C0_MUTEX_KEY
    j _mux_unlock_n_unlock
_hart1_unlock_n:
    li t1, 1
    bne t0, t1, _hart2_unlock_n
    li a0, C1_MUTEX_KEY
    j _mux_unlock_n_unlock
_hart2_unlock_n:
    li t1, 2
    bne t0, t1, _hart3_unlock_n
    li a0, C2_MUTEX_KEY 
    j _mux_unlock_n_unlock
_hart3_unlock_n:
    li a0, C3_MUTEX_KEY
_mux_unlock_n_unlock:
    lw t0, 0(a0)
    // Wait until the expected core is locked
    beqz t0, _mux_unlock_n_unlock 
    // Then unlock
    li t0, 0
    amoswap.w t0, t0, (a0)
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
    li t0, 0x8
    csrw mie, t0
    wfi
    csrw mie, zero
    amoswap.w t0, zero, (a0)
    li s0, 0x8
    ret
// ====== Semaphore signal ========
sem_signal:
    li t0, 1
    amoswap.w t1, t0, (a0)
    ret
// ====== Random integer ==========
randi:
    addi sp, sp, -16
    sd s0, 0(sp)
    sd s1, 8(sp)
    mv s0, a0 // limit
    csrr s1, mcycle
    remuw a0, s1, s0
    ld s0, 0(sp)
    ld s1, 8(sp)
    addi sp, sp, 16
    ret