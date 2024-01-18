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

## Prepare SD-card 
You'll need the gptfdisk tool to format the SD card

Download and install gptfdisk
```shell
git clone https://github.com/tmagik/gptfdisk.git
cd gptfdisk
make -j`nproc`
```

Connect the SD-card to your PC, then:
```shell
cd gptfdisk/
sudo ./gdisk /dev/sd?
```
Where ? points to the SD card.

After opening the SD card, check the existing partitions by pressing p, then Enter.
<img src="[drawing.jpg](https://github.com/binhkieudo/riscv_framework/assets/22954544/9ce04d3a-1c11-4ff5-b44c-31357dc6454c)" alt="drawing" width="200"/>
![image](https://github.com/binhkieudo/riscv_framework/assets/22954544/9ce04d3a-1c11-4ff5-b44c-31357dc6454c)

Delete all existing partitions by d <partition number>. 
![image](https://github.com/binhkieudo/riscv_framework/assets/22954544/4b09da7c-71f0-4e4f-b5e1-531424b109de)

Then format the SD Card with the following options:
![image](https://github.com/binhkieudo/riscv_framework/assets/22954544/901b67a2-32c9-47d9-8658-017c09a014dc)

After formatting the SD Card, print the new partition and save the configurations.
![image](https://github.com/binhkieudo/riscv_framework/assets/22954544/bffa4c08-977d-427d-9da4-8a4e0555ab19)

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
