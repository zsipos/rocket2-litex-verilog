set -e

UPSTREAM=https://github.com/chipsalliance/rocket-chip
RELEASE=v1.2.0

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
for CFG in LitexConfig LitexLinuxConfig LitexLinuxConfigDualCore LitexFullConfig
do
  for MEM in 64 128 256
  do
    for BITS in 32 64
    do
      if [ $CFG != LitexFullConfig ] || [ $BITS != 32 ]
      then
        make -C rocket-chip/vsim verilog \
          CONFIG=${CFG}${BITS}Mem${MEM} MODEL=LitexRocketSystem
      fi
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



