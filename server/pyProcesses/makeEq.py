import sys
import numpy
x = numpy.array(sys.argv[1].split(','), dtype = numpy.int32)
y = numpy.array(sys.argv[2].split(','), dtype = numpy.int32)
x = numpy.delete(x,0)

y = numpy.delete(y,0)
z = numpy.polyfit(numpy.log10(x),y,1)
#z[0] = z[0] * 2.3
a = z.tolist()
print(a)
sys.stdout.flush()
