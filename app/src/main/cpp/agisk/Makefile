CFLAGS+=-D_FILE_OFFSET_BITS=64
#CXXFLAGS+=-Wall -D_FILE_OFFSET_BITS=64 -D USE_UTF16
CXXFLAGS+=-Wall -D_FILE_OFFSET_BITS=64 
LDFLAGS+=-L./libs --static
LIB_OBJS=$(LIB_NAMES:=.o)
MBR_LIB_OBJS=$(MBR_LIBS:=.o)
LIB_HEADERS=$(LIB_NAMES:=.h)
DEPEND= makedepend $(CXXFLAGS)

src=$(wildcard *.cpp)
header=$(wildcard *.hpp)
objs=$(patsubst %.c,%.o,$(src))
agisk:	$(objs)
	$(CXX) $^ $(LDFLAGS) -lgpt -luuid -o agisk
	chmod a+x agisk
#	$(CXX) gpt_get.o $(LDFLAGS) -lsgdisk -luuid -lpopt -o Get.x

#
