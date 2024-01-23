#################################
# RISCV Toolchain
#################################
NHART ?= 0

ROOT_DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
BUILD_DIR := $(ROOT_DIR)build

CC=$(RISCV)/bin/riscv64-unknown-linux-gnu-g++
OBJCOPY=$(RISCV)/bin/riscv64-unknown-elf-objcopy
OBJDUMP=$(RISCV)/bin/riscv64-unknown-elf-objdump
CFLAGS=-pthread -march=rv64gc -mcmodel=medany -O0 -Wall -nostartfiles 
CFLAGS+= -fno-common -g -DENTROPY=0 -mabi=lp64d -DNONSMP_HART=$(NHART)
CFLAGS+= -I $(ROOT_DIR)/include -I.
LFLAGS=-static -L $(ROOT_DIR)/linker 

PBUS_CLK ?= 1000000


default : elf bin dump

elf := $(BUILD_DIR)/hello.elf
$(elf): hello.c
	mkdir -p $(BUILD_DIR)
	$(CC) $(CFLAGS) -DTL_CLK="$(PBUS_CLK)UL" $(LFLAGS) -o $@ hello.c

.PHONY: elf
elf: $(elf)

bin := $(BUILD_DIR)/hello.bin
$(bin): $(elf)
	mkdir -p $(BUILD_DIR)
	$(OBJCOPY) -O binary $< $@


.PHONY: bin
bin: $(bin)

dump := $(BUILD_DIR)/hello.dump
$(dump): $(elf)
	$(OBJDUMP) -D -S $< > $@

.PHONY: dump
dump: $(dump)

.PHONY: clean
clean::
	rm -rf $(BUILD_DIR)
