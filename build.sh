set -e

UPSTREAM=https://github.com/chipsalliance/rocket-chip
RELEASE=v1.2.0

BITS_TO_BUILD="32 64"
MEMS_TO_BUILD="64 128 256"
CFGS_TO_BUILD="LitexConfig LitexLinuxConfig LitexLinuxConfigDualCore LitexFullConfig"


if [ "$ROCKET_IS_SUBMODULE" != "true" ] || [ ! -d rocket-chip ]
then
  rm -rf rocket-chip
  git clone --recursive $UPSTREAM
  pushd rocket-chip
  git checkout $RELEASE
  git submodule update --init --recursive
 popd
fi

SCALASRC=rocket-chip/src/main/scala
pushd $SCALASRC
rm -f litex
ln -s ../../../../litex litex
popd

rm -rf rocket-chip/vsim/generated-src
for CFG in $CFGS_TO_BUILD
do
  for MEM in $MEMS_TO_BUILD
  do
    for BITS in $BITS_TO_BUILD
    do
      CONFIG=${CFG}${BITS}Mem${MEM}
      make -C rocket-chip/vsim verilog CONFIG=$CONFIG MODEL=LitexRocketSystem
    done
  done
done

DSTDIR=generated-src
rm -rf $DSTDIR
mkdir  $DSTDIR

VDIR=rocket-chip/src/main/resources/vsrc
GDIR=rocket-chip/vsim/generated-src
cp $VDIR/*     $DSTDIR
cp $GDIR/*.v   $DSTDIR
cp $GDIR/*.dts $DSTDIR
chmod 0644 $DSTDIR/*



