# File generated by petrify 4.1 (compiled Tue May  2 16:30:07  2000)
# from <aufgabe1.g> on 23-May-12 at 10:16 AM
# CPU time: 0.00 sec
#   0.00(trav)+0.00(init)+0.00(min)+0.00(regs)+0.00(irred)
# The original TS had (before/after minimization) 4/4 states
# Original STG:   4 places,   8 transitions,  16 arcs (  8 pt +  8 tp +  0 tt)
# Current STG:    0 places,   4 transitions,   4 arcs (  0 pt +  0 tp +  4 tt)
# It is a Marked Graph and a State Machine
.model a
.inputs  a b c d
.graph
a c
c a
b d
d b
.marking { <c,a> <d,b> }
.end
