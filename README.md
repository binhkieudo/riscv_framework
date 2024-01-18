# RISC-V Framework

## Instructions

Clone using:

```shell
git clone https://github.com/binhkieudo/riscv_framework.git
cd riscv_framework
git checkout dev
```

## Install conda enviroment

Download and install Anaconda first

```shell
wget https://repo.anaconda.com/archive/Anaconda3-2023.09-0-Linux-x86_64.sh
sudo chmod a+x ./Anaconda3-2023.09-0-Linux-x86_64.sh
./Anaconda3-2023.09-0-Linux-x86_64.sh
```

Then install conda-lock for "base" environment

```shell
conda install -n base conda-lock=1.4
```

## Build chipyard enviroment

Active coda first

```shell
conda activate
```

Then bulid the chipyard related tools

```shell
./build-setup.sh riscv-tools
```

In case that FireMarshal corrupt, install without FireMarshal

```shell
./build-setup.sh -s 9 riscv-tools
```

## Active chipyard environment

You have to active the chipyard environment whenever working with chipyard framework.

```shell
source env.sh
```

## Build your first FPGA prototype (currently support Arty100T)

Vivado must be in your PATH

Enter fpga folder
```shell
cd fpga
```

Build the bitstream with your selected configuration.
```shell
make SUB_PROJECT=arty100tTiny bitstream
```

After generating ".bit" file, connect the Arty board to your PC (make sure that the rules for Digilent cable are defined beforehand). Then program the board.
```shell
make download_bitstream
```
