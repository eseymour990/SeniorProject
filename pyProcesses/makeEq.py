import sys
import numpy
x = numpy.array(sys.argv[1].split(','), dtype = numpy.int32)
y = numpy.array(sys.argv[2].split(','), dtype = numpy.int32)
z = numpy.polyfit(numpy.log(x),y,1)
z[0] = z[0] * 2.3
print z
sys.stdout.flush()
