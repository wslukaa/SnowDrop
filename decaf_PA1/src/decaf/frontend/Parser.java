//### This file created by BYACC 1.8(/Java extension  1.13)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//###           14 Sep 06  -- Keltin Leung-- ReduceListener support, eliminate underflow report in error recovery
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 11 "Parser.y"
package decaf.frontend;

import decaf.tree.Tree;
import decaf.tree.Tree.*;
import decaf.error.*;
import java.util.*;
//#line 25 "Parser.java"
interface ReduceListener {
  public boolean onReduce(String rule);
}




public class Parser
             extends BaseParser
             implements ReduceListener
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

ReduceListener reduceListener = null;
void yyclearin ()       {yychar = (-1);}
void yyerrok ()         {yyerrflag=0;}
void addReduceListener(ReduceListener l) {
  reduceListener = l;}


//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//## **user defined:SemValue
String   yytext;//user variable to return contextual strings
SemValue yyval; //used to return semantic vals from action routines
SemValue yylval;//the 'lval' (result) I got from yylex()
SemValue valstk[] = new SemValue[YYSTACKSIZE];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
final void val_init()
{
  yyval=new SemValue();
  yylval=new SemValue();
  valptr=-1;
}
final void val_push(SemValue val)
{
  try {
    valptr++;
    valstk[valptr]=val;
  }
  catch (ArrayIndexOutOfBoundsException e) {
    int oldsize = valstk.length;
    int newsize = oldsize*2;
    SemValue[] newstack = new SemValue[newsize];
    System.arraycopy(valstk,0,newstack,0,oldsize);
    valstk = newstack;
    valstk[valptr]=val;
  }
}
final SemValue val_pop()
{
  return valstk[valptr--];
}
final void val_drop(int cnt)
{
  valptr -= cnt;
}
final SemValue val_peek(int relative)
{
  return valstk[valptr-relative];
}
//#### end semantic value section ####
public final static short VOID=257;
public final static short BOOL=258;
public final static short INT=259;
public final static short STRING=260;
public final static short CLASS=261;
public final static short NULL=262;
public final static short EXTENDS=263;
public final static short THIS=264;
public final static short WHILE=265;
public final static short FOR=266;
public final static short IF=267;
public final static short ELSE=268;
public final static short RETURN=269;
public final static short BREAK=270;
public final static short NEW=271;
public final static short PRINT=272;
public final static short READ_INTEGER=273;
public final static short READ_LINE=274;
public final static short LITERAL=275;
public final static short IDENTIFIER=276;
public final static short AND=277;
public final static short OR=278;
public final static short STATIC=279;
public final static short INSTANCEOF=280;
public final static short LESS_EQUAL=281;
public final static short GREATER_EQUAL=282;
public final static short EQUAL=283;
public final static short NOT_EQUAL=284;
public final static short SELF_ADD=285;
public final static short AELF_REDUCE=286;
public final static short TERNARY_FIRST=287;
public final static short TERNARY_SECOND=288;
public final static short NUMINSTANCES=289;
public final static short FI=290;
public final static short SEPARATOR=291;
public final static short DO=292;
public final static short OD=293;
public final static short UMINUS=294;
public final static short SELF_REDUCE=295;
public final static short EMPTY=296;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    3,    4,    5,    5,    5,    5,    5,
    5,    2,    6,    6,    7,    7,    7,    9,    9,   10,
   10,    8,    8,   11,   12,   12,   13,   13,   13,   13,
   13,   13,   13,   13,   13,   13,   13,   14,   14,   14,
   26,   26,   23,   23,   25,   24,   24,   24,   24,   24,
   24,   24,   24,   24,   24,   24,   24,   24,   24,   24,
   24,   24,   24,   24,   24,   24,   24,   24,   24,   24,
   24,   24,   24,   24,   24,   24,   24,   28,   28,   27,
   27,   29,   29,   16,   17,   20,   15,   30,   30,   18,
   18,   19,   31,   31,   21,   22,
};
final static short yylen[] = {                            2,
    1,    2,    1,    2,    2,    1,    1,    1,    1,    2,
    3,    6,    2,    0,    2,    2,    0,    1,    0,    3,
    1,    7,    6,    3,    2,    0,    1,    2,    1,    1,
    1,    2,    2,    2,    1,    1,    1,    3,    1,    0,
    2,    0,    2,    4,    5,    1,    1,    1,    3,    3,
    3,    3,    3,    3,    3,    3,    3,    3,    3,    3,
    3,    3,    2,    2,    3,    3,    1,    4,    5,    6,
    5,    2,    2,    2,    2,    5,    4,    1,    1,    1,
    0,    3,    1,    5,    9,    1,    6,    2,    0,    2,
    1,    4,    5,    3,    3,    3,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    3,    0,    2,    0,    0,   13,   17,
    0,    7,    8,    6,    9,    0,    0,   12,   15,    0,
    0,   16,   10,    0,    4,    0,    0,    0,    0,   11,
    0,   21,    0,    0,    0,    0,    5,    0,    0,    0,
   26,   23,   20,   22,    0,   79,   67,    0,    0,    0,
    0,   86,    0,    0,    0,    0,   78,    0,    0,    0,
    0,    0,    0,    0,   24,    0,   27,   35,   25,    0,
   29,   30,   31,    0,    0,    0,   36,   37,    0,    0,
    0,    0,   48,    0,    0,    0,   46,    0,   47,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   28,   32,   33,   34,    0,
    0,    0,    0,    0,    0,    0,   72,    0,    0,    0,
    0,    0,    0,    0,    0,   41,    0,   74,    0,    0,
    0,    0,    0,   95,    0,    0,    0,    0,    0,   65,
   66,    0,    0,   96,    0,   62,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   94,    0,   68,    0,
    0,   92,    0,   77,    0,    0,   44,    0,    0,   84,
    0,    0,    0,   69,    0,    0,   71,    0,   45,    0,
    0,   87,   93,   70,    0,   88,    0,   85,
};
final static short yydgoto[] = {                          2,
    3,    4,   67,   20,   33,    8,   11,   22,   34,   35,
   68,   45,   69,   70,   71,   72,   73,   74,   75,   76,
   77,   78,   87,   80,   89,   82,  178,   83,  139,  192,
   90,
};
final static short yysindex[] = {                      -229,
 -242,    0, -229,    0, -223,    0, -235,  -81,    0,    0,
  -77,    0,    0,    0,    0, -233, -176,    0,    0,    3,
  -89,    0,    0,  -86,    0,   23,  -27,   28, -176,    0,
 -176,    0,  -83,   29,   27,   32,    0,  -49, -176,  -49,
    0,    0,    0,    0,    6,    0,    0,   36,   37,  106,
  114,    0, -201,   47,   48,   49,    0,   53,  114,   54,
  114,  114,  114,   72,    0,  114,    0,    0,    0,   38,
    0,    0,    0,   40,   41,   43,    0,    0,   34,  883,
    0, -180,    0,  114,  114,   72,    0,  475,    0, -253,
  883,   63,   13,  114,   67,   74,  114,  -30, -158, -273,
  -11,  -11, -155,  497,  -30,    0,    0,    0,    0,  114,
  114,  114,  114,  114,  114,  114,    0,  114,  114,  114,
  114,  114,  114,  114,  114,    0,  114,    0,   82,  527,
   69,  549,   46,    0,  114,   85,   80,  883,  -22,    0,
    0,  570,   91,    0,   92,    0,  883,  942,  935,   64,
   64,    7,    7,  600,   77,   77,  -11,  -11,  -11,   64,
   64,  628,  114,   46,  114,  159,    0,  680,    0,  778,
  114,    0, -142,    0,  114,  114,    0,   94,   93,    0,
  857, -132,   46,    0,  883,   97,    0,  883,    0,  114,
   46,    0,    0,    0,   99,    0,   46,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,  141,    0,   19,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   84,    0,    0,  103,    0,
  103,    0,    0,    0,  107,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  -58,    0,    0,    0,    0, -127,
  -55,    0,    0,    0,    0,    0,    0,    0, -127,    0,
 -127, -127, -127, -127,    0, -127,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  909,    0,
  454,    0,    0, -127,  -58, -127,    0,    0,    0,    0,
   98,    0,    0, -127,    0,    0, -127,  975,    0,    0,
 1007, 1033,    0,    0, 1060,    0,    0,    0,    0, -127,
 -127, -127, -127, -127, -127, -127,    0, -127, -127, -127,
 -127, -127, -127, -127, -127,    0, -127,    0,  399,    0,
    0,    0,  -58,    0, -127,    0, -127,  -17,    0,    0,
    0,    0,    0,    0,    0,    0,  -28,   86,  126,  489,
  512,  119,  585,    0, 1188, 1263, 1086, 1334, 1358,  988,
 1156,    0,  -32,  -58, -127,  420,    0,    0,    0,    0,
 -127,    0,    0,    0, -127, -127,    0,    0,  109,    0,
    0,  -33,  -58,    0,   -8,    0,    0,  -38,    0,  -31,
  -58,    0,    0,    0,    0,    0,  -58,    0,
};
final static short yygindex[] = {                         0,
    0,  149,  142,  -14,   12,    0,    0,    0,  125,    0,
  -10,    0, -119,  -74,    0,    0,    0,    0,    0,    0,
    0,    0,  444, 1395, 1136,    0,    0,    0,   -5,    0,
  100,
};
final static int YYTABLESIZE=1646;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         89,
   40,   27,   76,   91,   27,   76,   89,   27,   81,   40,
  131,   89,   38,  167,   32,  126,   32,  135,  172,  144,
   76,  171,   21,   83,   43,   89,   83,   42,   24,   44,
   38,    1,   82,    5,  126,   82,  134,  135,   63,    7,
    9,   10,   23,  123,  180,   64,  182,   18,  121,  119,
   62,  120,  126,  122,   76,   12,   13,   14,   15,   16,
  127,   25,   29,  193,   93,   30,  125,   31,  124,   38,
   39,  196,   40,   41,   92,   84,   85,  198,   63,  127,
   12,   13,   14,   15,   16,   64,   94,   95,   96,   89,
   62,   89,   97,   99,  110,  129,  106,  127,  107,  108,
  123,  109,  136,  137,   63,  121,  119,  140,  120,  126,
  122,   64,   63,  123,  141,  195,   62,  143,  121,   64,
  145,  163,  126,  122,   62,  169,   60,  165,   41,   60,
   65,  174,  175,  186,  189,  191,  171,  194,   63,  197,
    1,   14,    5,   19,   60,   86,   63,   18,   42,   80,
   62,    6,   19,   64,  127,   36,   90,  179,   62,   54,
  100,    0,   54,    0,    0,    0,   61,  127,   41,   61,
    0,    0,   30,    0,    0,    0,    0,   54,   60,   12,
   13,   14,   15,   16,   61,    0,   26,    0,    0,   28,
    0,   63,   37,    0,    0,    0,    0,    0,   64,    0,
    0,   17,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   54,    0,    0,    0,    0,    0,   42,   61,    0,
   42,    0,    0,   89,   89,   89,   89,   89,   89,    0,
   89,   89,   89,   89,    0,   89,   89,   89,   89,   89,
   89,   89,   89,   42,   42,    0,   89,    0,    0,   76,
    0,   89,    0,    0,    0,   89,   89,   89,   89,   89,
    0,   89,   12,   13,   14,   15,   16,   46,    0,   47,
   48,   49,   50,  117,   51,   52,   53,   54,   55,   56,
   57,   41,    0,  128,    0,   58,    0,  113,  114,    0,
   59,  117,    0,    0,   60,    0,    0,   61,    0,    0,
   66,  128,   12,   13,   14,   15,   16,   46,    0,   47,
   48,   49,   50,    0,   51,   52,   53,   54,   55,   56,
   57,    0,    0,    0,    0,   58,    0,    0,    0,    0,
   59,    0,  103,   46,   60,   47,    0,   61,    0,    0,
   66,   46,   53,   47,   55,   56,   57,    0,  117,    0,
   53,   58,   55,   56,   57,    0,   59,    0,  128,   58,
   60,  117,   60,   60,   59,    0,   66,   46,   60,   47,
    0,  128,   60,   60,   66,   46,   53,   47,   55,   56,
   57,    0,    0,    0,   53,   58,   55,   56,   57,    0,
   59,    0,    0,   58,   60,   54,   54,    0,   59,    0,
   66,    0,   60,   61,    0,   54,   54,    0,   66,    0,
    0,    0,   61,   61,    0,   12,   13,   14,   15,   16,
   46,    0,   47,   48,   49,   50,    0,   51,   52,   53,
   54,   55,   56,   57,    0,   43,    0,    0,   58,   43,
   43,   43,   43,   43,   43,   43,    0,   60,    0,    0,
   61,    0,    0,    0,    0,    0,   62,   43,   43,   43,
   43,   62,   62,    0,   62,   62,   62,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   40,   62,
    0,   62,    0,    0,    0,    0,    0,    0,   79,   43,
   47,   43,    0,    0,   39,   47,   47,    0,   47,   47,
   47,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   62,  123,   39,   47,    0,   47,  121,  119,    0,  120,
  126,  122,    0,    0,    0,    0,    0,    0,   79,   58,
    0,    0,   58,  123,  125,    0,  124,  146,  121,  119,
    0,  120,  126,  122,   47,    0,    0,   58,    0,    0,
    0,    0,   59,    0,    0,   59,  125,    0,  124,    0,
    0,    0,    0,  123,    0,  127,    0,  164,  121,  119,
   59,  120,  126,  122,    0,    0,   79,    0,    0,    0,
    0,   58,    0,    0,    0,  123,  125,  127,  124,  166,
  121,  119,    0,  120,  126,  122,    0,    0,    0,    0,
    0,    0,    0,    0,   59,    0,  123,   79,  125,   79,
  124,  121,  119,  173,  120,  126,  122,  127,    0,    0,
    0,    0,    0,    0,    0,   55,   79,    0,   55,  125,
    0,  124,    0,   79,   79,    0,  123,    0,    0,  127,
   79,  121,  119,   55,  120,  126,  122,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  125,
  127,  124,    0,    0,  123,    0,    0,    0,    0,  121,
  119,    0,  120,  126,  122,   43,   43,   55,    0,   43,
   43,   43,   43,   43,    0,   43,   43,  125,    0,  124,
  127,    0,    0,   43,    0,   42,   62,   62,    0,    0,
   62,   62,   62,   62,   62,    0,   62,   62,    0,    0,
    0,    0,    0,    0,   62,    0,  123,    0,  127,    0,
  177,  121,  119,    0,  120,  126,  122,    0,    0,    0,
   47,   47,    0,    0,   47,   47,   47,   47,   47,  125,
   47,  124,    0,    0,    0,    0,    0,    0,   47,    0,
    0,  111,  112,    0,    0,  113,  114,  115,  116,  117,
    0,  118,  133,    0,    0,   58,   58,    0,    0,  128,
  127,   58,   58,  111,  112,   58,   58,  113,  114,  115,
  116,  117,    0,  118,    0,    0,    0,    0,   59,   59,
    0,  128,    0,    0,   59,   59,    0,    0,   59,   59,
    0,    0,    0,  111,  112,    0,    0,  113,  114,  115,
  116,  117,    0,  118,  123,    0,    0,    0,    0,  121,
  119,  128,  120,  126,  122,  111,  112,    0,    0,  113,
  114,  115,  116,  117,    0,  118,    0,  125,    0,  124,
    0,    0,    0,  128,    0,    0,  111,  112,    0,    0,
  113,  114,  115,  116,  117,    0,  118,    0,    0,    0,
    0,   55,   55,    0,  128,    0,    0,    0,  127,    0,
  184,   55,   55,    0,    0,    0,  111,  112,    0,    0,
  113,  114,  115,  116,  117,    0,  118,  176,    0,    0,
    0,    0,    0,  123,  128,    0,    0,    0,  121,  119,
    0,  120,  126,  122,  111,  112,    0,    0,  113,  114,
  115,  116,  117,    0,  118,  190,  125,    0,  124,  123,
    0,    0,  128,    0,  121,  119,    0,  120,  126,  122,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  125,    0,  124,   46,    0,  127,    0,    0,
   46,   46,    0,   46,   46,   46,  111,  112,    0,    0,
  113,  114,  115,  116,  117,    0,  118,  183,   46,    0,
   46,  123,    0,  127,  128,    0,  121,  119,  123,  120,
  126,  122,    0,  121,  119,    0,  120,  126,  122,    0,
    0,    0,    0,    0,  125,    0,  124,    0,    0,   46,
    0,  125,    0,  124,    0,    0,    0,    0,    0,    0,
    0,   73,    0,    0,    0,   73,   73,   73,   73,   73,
    0,   73,    0,    0,    0,  127,    0,    0,   57,    0,
    0,   57,  127,   73,   73,    0,   73,    0,    0,    0,
    0,    0,    0,   63,    0,    0,   57,   63,   63,   63,
   63,   63,    0,   63,  111,  112,    0,    0,  113,  114,
  115,  116,  117,    0,  118,   63,   63,   73,   63,   64,
    0,    0,  128,   64,   64,   64,   64,   64,    0,   64,
   57,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   64,   64,    0,   64,    0,   75,    0,    0,   63,
   75,   75,   75,   75,   75,    0,   75,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   75,   75,
    0,   75,   51,    0,    0,   64,   51,   51,   51,   51,
   51,    0,   51,  111,  112,    0,    0,  113,  114,  115,
  116,  117,    0,  118,   51,   51,    0,   51,    0,    0,
    0,  128,   75,    0,    0,    0,    0,    0,    0,  111,
  112,    0,    0,  113,  114,  115,  116,  117,    0,  118,
    0,    0,    0,    0,    0,    0,    0,  128,   51,    0,
   81,    0,    0,    0,    0,   46,   46,    0,    0,   46,
   46,   46,   46,   46,    0,   46,   56,    0,    0,   56,
    0,    0,    0,   46,    0,    0,    0,    0,    0,    0,
    0,  111,    0,    0,   56,  113,  114,  115,  116,  117,
   81,    0,  113,  114,  115,  116,  117,    0,   49,  128,
   49,   49,   49,    0,    0,    0,  128,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   49,   49,   56,   49,
    0,   73,   73,    0,    0,   73,   73,   73,   73,    0,
    0,   73,   73,    0,   57,   57,    0,    0,   81,    0,
   57,   57,    0,    0,   57,   57,    0,    0,    0,    0,
   49,    0,    0,   63,   63,    0,    0,   63,   63,   63,
   63,    0,    0,   63,   63,    0,    0,    0,    0,   81,
    0,   81,    0,   50,    0,   50,   50,   50,    0,   64,
   64,    0,    0,   64,   64,   64,   64,    0,   81,   64,
   64,   50,   50,    0,   50,   81,   81,    0,    0,    0,
    0,    0,   81,    0,    0,    0,   75,   75,    0,    0,
   75,   75,   75,   75,    0,    0,   75,   75,    0,    0,
    0,    0,    0,    0,    0,   50,    0,    0,    0,    0,
    0,    0,   51,   51,    0,    0,   51,   51,   51,   51,
   52,    0,   51,   51,   52,   52,   52,   52,   52,    0,
   52,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   52,   52,   53,   52,    0,    0,   53,   53,
   53,   53,   53,    0,   53,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   53,   53,    0,   53,
    0,    0,    0,    0,    0,    0,   52,    0,    0,    0,
    0,    0,   56,   56,    0,    0,    0,    0,   56,   56,
    0,    0,   56,   56,   88,   91,    0,    0,    0,    0,
   53,    0,    0,   98,    0,   88,  101,  102,  104,    0,
  105,    0,    0,    0,   49,   49,    0,    0,   49,   49,
   49,   49,    0,    0,   49,   49,    0,    0,  130,    0,
  132,    0,    0,    0,    0,    0,    0,    0,  138,    0,
    0,  142,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  147,  148,  149,  150,  151,  152,
  153,    0,  154,  155,  156,  157,  158,  159,  160,  161,
    0,  162,    0,    0,    0,    0,    0,    0,    0,  168,
    0,  170,    0,    0,    0,    0,    0,    0,    0,   50,
   50,    0,    0,   50,   50,   50,   50,    0,    0,   50,
   50,    0,    0,    0,    0,    0,    0,  138,    0,  181,
    0,    0,    0,    0,    0,  185,    0,    0,    0,  187,
  188,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   52,   52,    0,    0,   52,   52,   52,   52,    0,    0,
   52,   52,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   53,   53,    0,    0,   53,   53,
   53,   53,    0,    0,   53,   53,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         33,
   59,   91,   41,   59,   91,   44,   40,   91,   41,   41,
   85,   45,   41,  133,   29,   46,   31,  291,   41,  293,
   59,   44,   11,   41,   39,   59,   44,   38,   17,   40,
   59,  261,   41,  276,   46,   44,  290,  291,   33,  263,
  276,  123,  276,   37,  164,   40,  166,  125,   42,   43,
   45,   45,   46,   47,   93,  257,  258,  259,  260,  261,
   91,   59,   40,  183,   53,   93,   60,   40,   62,   41,
   44,  191,   41,  123,  276,   40,   40,  197,   33,   91,
  257,  258,  259,  260,  261,   40,   40,   40,   40,  123,
   45,  125,   40,   40,   61,  276,   59,   91,   59,   59,
   37,   59,   40,   91,   33,   42,   43,   41,   45,   46,
   47,   40,   33,   37,   41,  190,   45,  276,   42,   40,
  276,   40,   46,   47,   45,   41,   41,   59,  123,   44,
  125,   41,   41,  276,   41,  268,   44,   41,   33,   41,
    0,  123,   59,   41,   59,   40,   33,   41,  276,   41,
   45,    3,   11,   40,   91,   31,   59,  163,   45,   41,
   61,   -1,   44,   -1,   -1,   -1,   41,   91,  123,   44,
   -1,   -1,   93,   -1,   -1,   -1,   -1,   59,   93,  257,
  258,  259,  260,  261,   59,   -1,  276,   -1,   -1,  276,
   -1,   33,  276,   -1,   -1,   -1,   -1,   -1,   40,   -1,
   -1,  279,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   93,   -1,   -1,   -1,   -1,   -1,  276,   93,   -1,
  276,   -1,   -1,  257,  258,  259,  260,  261,  262,   -1,
  264,  265,  266,  267,   -1,  269,  270,  271,  272,  273,
  274,  275,  276,  276,  276,   -1,  280,   -1,   -1,  288,
   -1,  285,   -1,   -1,   -1,  289,  290,  291,  292,  293,
   -1,  295,  257,  258,  259,  260,  261,  262,   -1,  264,
  265,  266,  267,  285,  269,  270,  271,  272,  273,  274,
  275,  123,   -1,  295,   -1,  280,   -1,  281,  282,   -1,
  285,  285,   -1,   -1,  289,   -1,   -1,  292,   -1,   -1,
  295,  295,  257,  258,  259,  260,  261,  262,   -1,  264,
  265,  266,  267,   -1,  269,  270,  271,  272,  273,  274,
  275,   -1,   -1,   -1,   -1,  280,   -1,   -1,   -1,   -1,
  285,   -1,  261,  262,  289,  264,   -1,  292,   -1,   -1,
  295,  262,  271,  264,  273,  274,  275,   -1,  285,   -1,
  271,  280,  273,  274,  275,   -1,  285,   -1,  295,  280,
  289,  285,  277,  278,  285,   -1,  295,  262,  289,  264,
   -1,  295,  287,  288,  295,  262,  271,  264,  273,  274,
  275,   -1,   -1,   -1,  271,  280,  273,  274,  275,   -1,
  285,   -1,   -1,  280,  289,  277,  278,   -1,  285,   -1,
  295,   -1,  289,  278,   -1,  287,  288,   -1,  295,   -1,
   -1,   -1,  287,  288,   -1,  257,  258,  259,  260,  261,
  262,   -1,  264,  265,  266,  267,   -1,  269,  270,  271,
  272,  273,  274,  275,   -1,   37,   -1,   -1,  280,   41,
   42,   43,   44,   45,   46,   47,   -1,  289,   -1,   -1,
  292,   -1,   -1,   -1,   -1,   -1,   37,   59,   60,   61,
   62,   42,   43,   -1,   45,   46,   47,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   59,   60,
   -1,   62,   -1,   -1,   -1,   -1,   -1,   -1,   45,   91,
   37,   93,   -1,   -1,   41,   42,   43,   -1,   45,   46,
   47,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   91,   37,   59,   60,   -1,   62,   42,   43,   -1,   45,
   46,   47,   -1,   -1,   -1,   -1,   -1,   -1,   85,   41,
   -1,   -1,   44,   37,   60,   -1,   62,   41,   42,   43,
   -1,   45,   46,   47,   91,   -1,   -1,   59,   -1,   -1,
   -1,   -1,   41,   -1,   -1,   44,   60,   -1,   62,   -1,
   -1,   -1,   -1,   37,   -1,   91,   -1,   41,   42,   43,
   59,   45,   46,   47,   -1,   -1,  133,   -1,   -1,   -1,
   -1,   93,   -1,   -1,   -1,   37,   60,   91,   62,   41,
   42,   43,   -1,   45,   46,   47,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   93,   -1,   37,  164,   60,  166,
   62,   42,   43,   44,   45,   46,   47,   91,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   41,  183,   -1,   44,   60,
   -1,   62,   -1,  190,  191,   -1,   37,   -1,   -1,   91,
  197,   42,   43,   59,   45,   46,   47,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   60,
   91,   62,   -1,   -1,   37,   -1,   -1,   -1,   -1,   42,
   43,   -1,   45,   46,   47,  277,  278,   93,   -1,  281,
  282,  283,  284,  285,   -1,  287,  288,   60,   -1,   62,
   91,   -1,   -1,  295,   -1,  276,  277,  278,   -1,   -1,
  281,  282,  283,  284,  285,   -1,  287,  288,   -1,   -1,
   -1,   -1,   -1,   -1,  295,   -1,   37,   -1,   91,   -1,
   93,   42,   43,   -1,   45,   46,   47,   -1,   -1,   -1,
  277,  278,   -1,   -1,  281,  282,  283,  284,  285,   60,
  287,   62,   -1,   -1,   -1,   -1,   -1,   -1,  295,   -1,
   -1,  277,  278,   -1,   -1,  281,  282,  283,  284,  285,
   -1,  287,  288,   -1,   -1,  277,  278,   -1,   -1,  295,
   91,  283,  284,  277,  278,  287,  288,  281,  282,  283,
  284,  285,   -1,  287,   -1,   -1,   -1,   -1,  277,  278,
   -1,  295,   -1,   -1,  283,  284,   -1,   -1,  287,  288,
   -1,   -1,   -1,  277,  278,   -1,   -1,  281,  282,  283,
  284,  285,   -1,  287,   37,   -1,   -1,   -1,   -1,   42,
   43,  295,   45,   46,   47,  277,  278,   -1,   -1,  281,
  282,  283,  284,  285,   -1,  287,   -1,   60,   -1,   62,
   -1,   -1,   -1,  295,   -1,   -1,  277,  278,   -1,   -1,
  281,  282,  283,  284,  285,   -1,  287,   -1,   -1,   -1,
   -1,  277,  278,   -1,  295,   -1,   -1,   -1,   91,   -1,
   93,  287,  288,   -1,   -1,   -1,  277,  278,   -1,   -1,
  281,  282,  283,  284,  285,   -1,  287,  288,   -1,   -1,
   -1,   -1,   -1,   37,  295,   -1,   -1,   -1,   42,   43,
   -1,   45,   46,   47,  277,  278,   -1,   -1,  281,  282,
  283,  284,  285,   -1,  287,   59,   60,   -1,   62,   37,
   -1,   -1,  295,   -1,   42,   43,   -1,   45,   46,   47,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   60,   -1,   62,   37,   -1,   91,   -1,   -1,
   42,   43,   -1,   45,   46,   47,  277,  278,   -1,   -1,
  281,  282,  283,  284,  285,   -1,  287,  288,   60,   -1,
   62,   37,   -1,   91,  295,   -1,   42,   43,   37,   45,
   46,   47,   -1,   42,   43,   -1,   45,   46,   47,   -1,
   -1,   -1,   -1,   -1,   60,   -1,   62,   -1,   -1,   91,
   -1,   60,   -1,   62,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   37,   -1,   -1,   -1,   41,   42,   43,   44,   45,
   -1,   47,   -1,   -1,   -1,   91,   -1,   -1,   41,   -1,
   -1,   44,   91,   59,   60,   -1,   62,   -1,   -1,   -1,
   -1,   -1,   -1,   37,   -1,   -1,   59,   41,   42,   43,
   44,   45,   -1,   47,  277,  278,   -1,   -1,  281,  282,
  283,  284,  285,   -1,  287,   59,   60,   93,   62,   37,
   -1,   -1,  295,   41,   42,   43,   44,   45,   -1,   47,
   93,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   59,   60,   -1,   62,   -1,   37,   -1,   -1,   93,
   41,   42,   43,   44,   45,   -1,   47,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   59,   60,
   -1,   62,   37,   -1,   -1,   93,   41,   42,   43,   44,
   45,   -1,   47,  277,  278,   -1,   -1,  281,  282,  283,
  284,  285,   -1,  287,   59,   60,   -1,   62,   -1,   -1,
   -1,  295,   93,   -1,   -1,   -1,   -1,   -1,   -1,  277,
  278,   -1,   -1,  281,  282,  283,  284,  285,   -1,  287,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  295,   93,   -1,
   45,   -1,   -1,   -1,   -1,  277,  278,   -1,   -1,  281,
  282,  283,  284,  285,   -1,  287,   41,   -1,   -1,   44,
   -1,   -1,   -1,  295,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  277,   -1,   -1,   59,  281,  282,  283,  284,  285,
   85,   -1,  281,  282,  283,  284,  285,   -1,   41,  295,
   43,   44,   45,   -1,   -1,   -1,  295,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   59,   60,   93,   62,
   -1,  277,  278,   -1,   -1,  281,  282,  283,  284,   -1,
   -1,  287,  288,   -1,  277,  278,   -1,   -1,  133,   -1,
  283,  284,   -1,   -1,  287,  288,   -1,   -1,   -1,   -1,
   93,   -1,   -1,  277,  278,   -1,   -1,  281,  282,  283,
  284,   -1,   -1,  287,  288,   -1,   -1,   -1,   -1,  164,
   -1,  166,   -1,   41,   -1,   43,   44,   45,   -1,  277,
  278,   -1,   -1,  281,  282,  283,  284,   -1,  183,  287,
  288,   59,   60,   -1,   62,  190,  191,   -1,   -1,   -1,
   -1,   -1,  197,   -1,   -1,   -1,  277,  278,   -1,   -1,
  281,  282,  283,  284,   -1,   -1,  287,  288,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   93,   -1,   -1,   -1,   -1,
   -1,   -1,  277,  278,   -1,   -1,  281,  282,  283,  284,
   37,   -1,  287,  288,   41,   42,   43,   44,   45,   -1,
   47,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   59,   60,   37,   62,   -1,   -1,   41,   42,
   43,   44,   45,   -1,   47,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   59,   60,   -1,   62,
   -1,   -1,   -1,   -1,   -1,   -1,   93,   -1,   -1,   -1,
   -1,   -1,  277,  278,   -1,   -1,   -1,   -1,  283,  284,
   -1,   -1,  287,  288,   50,   51,   -1,   -1,   -1,   -1,
   93,   -1,   -1,   59,   -1,   61,   62,   63,   64,   -1,
   66,   -1,   -1,   -1,  277,  278,   -1,   -1,  281,  282,
  283,  284,   -1,   -1,  287,  288,   -1,   -1,   84,   -1,
   86,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   94,   -1,
   -1,   97,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  110,  111,  112,  113,  114,  115,
  116,   -1,  118,  119,  120,  121,  122,  123,  124,  125,
   -1,  127,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  135,
   -1,  137,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  277,
  278,   -1,   -1,  281,  282,  283,  284,   -1,   -1,  287,
  288,   -1,   -1,   -1,   -1,   -1,   -1,  163,   -1,  165,
   -1,   -1,   -1,   -1,   -1,  171,   -1,   -1,   -1,  175,
  176,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  277,  278,   -1,   -1,  281,  282,  283,  284,   -1,   -1,
  287,  288,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  277,  278,   -1,   -1,  281,  282,
  283,  284,   -1,   -1,  287,  288,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=296;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"'!'",null,null,null,"'%'",null,null,"'('","')'","'*'","'+'",
"','","'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,null,
"';'","'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,"'['",null,"']'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,"VOID","BOOL","INT","STRING",
"CLASS","NULL","EXTENDS","THIS","WHILE","FOR","IF","ELSE","RETURN","BREAK",
"NEW","PRINT","READ_INTEGER","READ_LINE","LITERAL","IDENTIFIER","AND","OR",
"STATIC","INSTANCEOF","LESS_EQUAL","GREATER_EQUAL","EQUAL","NOT_EQUAL",
"SELF_ADD","AELF_REDUCE","TERNARY_FIRST","TERNARY_SECOND","NUMINSTANCES","FI",
"SEPARATOR","DO","OD","UMINUS","SELF_REDUCE","EMPTY",
};
final static String yyrule[] = {
"$accept : Program",
"Program : ClassList",
"ClassList : ClassList ClassDef",
"ClassList : ClassDef",
"VariableDef : Variable ';'",
"Variable : Type IDENTIFIER",
"Type : INT",
"Type : VOID",
"Type : BOOL",
"Type : STRING",
"Type : CLASS IDENTIFIER",
"Type : Type '[' ']'",
"ClassDef : CLASS IDENTIFIER ExtendsClause '{' FieldList '}'",
"ExtendsClause : EXTENDS IDENTIFIER",
"ExtendsClause :",
"FieldList : FieldList VariableDef",
"FieldList : FieldList FunctionDef",
"FieldList :",
"Formals : VariableList",
"Formals :",
"VariableList : VariableList ',' Variable",
"VariableList : Variable",
"FunctionDef : STATIC Type IDENTIFIER '(' Formals ')' StmtBlock",
"FunctionDef : Type IDENTIFIER '(' Formals ')' StmtBlock",
"StmtBlock : '{' StmtList '}'",
"StmtList : StmtList Stmt",
"StmtList :",
"Stmt : VariableDef",
"Stmt : SimpleStmt ';'",
"Stmt : IfStmt",
"Stmt : WhileStmt",
"Stmt : ForStmt",
"Stmt : ReturnStmt ';'",
"Stmt : PrintStmt ';'",
"Stmt : BreakStmt ';'",
"Stmt : StmtBlock",
"Stmt : GuardedIfStmt",
"Stmt : GuardedDoStmt",
"SimpleStmt : LValue '=' Expr",
"SimpleStmt : Call",
"SimpleStmt :",
"Receiver : Expr '.'",
"Receiver :",
"LValue : Receiver IDENTIFIER",
"LValue : Expr '[' Expr ']'",
"Call : Receiver IDENTIFIER '(' Actuals ')'",
"Expr : LValue",
"Expr : Call",
"Expr : Constant",
"Expr : Expr '+' Expr",
"Expr : Expr '-' Expr",
"Expr : Expr '*' Expr",
"Expr : Expr '/' Expr",
"Expr : Expr '%' Expr",
"Expr : Expr EQUAL Expr",
"Expr : Expr NOT_EQUAL Expr",
"Expr : Expr '<' Expr",
"Expr : Expr '>' Expr",
"Expr : Expr LESS_EQUAL Expr",
"Expr : Expr GREATER_EQUAL Expr",
"Expr : Expr AND Expr",
"Expr : Expr OR Expr",
"Expr : '(' Expr ')'",
"Expr : '-' Expr",
"Expr : '!' Expr",
"Expr : READ_INTEGER '(' ')'",
"Expr : READ_LINE '(' ')'",
"Expr : THIS",
"Expr : NEW IDENTIFIER '(' ')'",
"Expr : NEW Type '[' Expr ']'",
"Expr : INSTANCEOF '(' Expr ',' IDENTIFIER ')'",
"Expr : '(' CLASS IDENTIFIER ')' Expr",
"Expr : Expr SELF_ADD",
"Expr : SELF_ADD Expr",
"Expr : Expr SELF_REDUCE",
"Expr : SELF_REDUCE Expr",
"Expr : Expr TERNARY_FIRST Expr TERNARY_SECOND Expr",
"Expr : NUMINSTANCES '(' IDENTIFIER ')'",
"Constant : LITERAL",
"Constant : NULL",
"Actuals : ExprList",
"Actuals :",
"ExprList : ExprList ',' Expr",
"ExprList : Expr",
"WhileStmt : WHILE '(' Expr ')' Stmt",
"ForStmt : FOR '(' SimpleStmt ';' Expr ';' SimpleStmt ')' Stmt",
"BreakStmt : BREAK",
"IfStmt : IF '(' Expr ')' Stmt ElseClause",
"ElseClause : ELSE Stmt",
"ElseClause :",
"ReturnStmt : RETURN Expr",
"ReturnStmt : RETURN",
"PrintStmt : PRINT '(' ExprList ')'",
"GuardedStmts : GuardedStmts SEPARATOR Expr TERNARY_SECOND Stmt",
"GuardedStmts : Expr TERNARY_SECOND Stmt",
"GuardedIfStmt : IF GuardedStmts FI",
"GuardedDoStmt : DO GuardedStmts OD",
};

//#line 482 "Parser.y"
    
	/**
	 * 打印当前归约所用的语法规则<br>
	 * 请勿修改。
	 */
    public boolean onReduce(String rule) {
		if (rule.startsWith("$$"))
			return false;
		else
			rule = rule.replaceAll(" \\$\\$\\d+", "");

   	    if (rule.endsWith(":"))
    	    System.out.println(rule + " <empty>");
   	    else
			System.out.println(rule);
		return false;
    }
    
    public void diagnose() {
		addReduceListener(this);
		yyparse();
	}
//#line 739 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    //if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      //if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        //if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        //if (yychar < 0)    //it it didn't work/error
        //  {
        //  yychar = 0;      //change it to default string (no -1!)
          //if (yydebug)
          //  yylexdebug(yystate,yychar);
        //  }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        //if (yydebug)
          //debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      //if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0 || valptr<0)   //check for under & overflow here
            {
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            //if (yydebug)
              //debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            //if (yydebug)
              //debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0 || valptr<0)   //check for under & overflow here
              {
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        //if (yydebug)
          //{
          //yys = null;
          //if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          //if (yys == null) yys = "illegal-symbol";
          //debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          //}
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    //if (yydebug)
      //debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    if (reduceListener == null || reduceListener.onReduce(yyrule[yyn])) // if intercepted!
      switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 59 "Parser.y"
{
						tree = new Tree.TopLevel(val_peek(0).clist, val_peek(0).loc);
					}
break;
case 2:
//#line 65 "Parser.y"
{
						yyval.clist.add(val_peek(0).cdef);
					}
break;
case 3:
//#line 69 "Parser.y"
{
                		yyval.clist = new ArrayList<Tree.ClassDef>();
                		yyval.clist.add(val_peek(0).cdef);
                	}
break;
case 5:
//#line 79 "Parser.y"
{
						yyval.vdef = new Tree.VarDef(val_peek(0).ident, val_peek(1).type, val_peek(0).loc);
					}
break;
case 6:
//#line 85 "Parser.y"
{
						yyval.type = new Tree.TypeIdent(Tree.INT, val_peek(0).loc);
					}
break;
case 7:
//#line 89 "Parser.y"
{
                		yyval.type = new Tree.TypeIdent(Tree.VOID, val_peek(0).loc);
                	}
break;
case 8:
//#line 93 "Parser.y"
{
                		yyval.type = new Tree.TypeIdent(Tree.BOOL, val_peek(0).loc);
                	}
break;
case 9:
//#line 97 "Parser.y"
{
                		yyval.type = new Tree.TypeIdent(Tree.STRING, val_peek(0).loc);
                	}
break;
case 10:
//#line 101 "Parser.y"
{
                		yyval.type = new Tree.TypeClass(val_peek(0).ident, val_peek(1).loc);
                	}
break;
case 11:
//#line 105 "Parser.y"
{
                		yyval.type = new Tree.TypeArray(val_peek(2).type, val_peek(2).loc);
                	}
break;
case 12:
//#line 111 "Parser.y"
{
						yyval.cdef = new Tree.ClassDef(val_peek(4).ident, val_peek(3).ident, val_peek(1).flist, val_peek(5).loc);
					}
break;
case 13:
//#line 117 "Parser.y"
{
						yyval.ident = val_peek(0).ident;
					}
break;
case 14:
//#line 121 "Parser.y"
{
                		yyval = new SemValue();
                	}
break;
case 15:
//#line 127 "Parser.y"
{
						yyval.flist.add(val_peek(0).vdef);
					}
break;
case 16:
//#line 131 "Parser.y"
{
						yyval.flist.add(val_peek(0).fdef);
					}
break;
case 17:
//#line 135 "Parser.y"
{
                		yyval = new SemValue();
                		yyval.flist = new ArrayList<Tree>();
                	}
break;
case 19:
//#line 143 "Parser.y"
{
                		yyval = new SemValue();
                		yyval.vlist = new ArrayList<Tree.VarDef>(); 
                	}
break;
case 20:
//#line 150 "Parser.y"
{
						yyval.vlist.add(val_peek(0).vdef);
					}
break;
case 21:
//#line 154 "Parser.y"
{
                		yyval.vlist = new ArrayList<Tree.VarDef>();
						yyval.vlist.add(val_peek(0).vdef);
                	}
break;
case 22:
//#line 161 "Parser.y"
{
						yyval.fdef = new MethodDef(true, val_peek(4).ident, val_peek(5).type, val_peek(2).vlist, (Block) val_peek(0).stmt, val_peek(4).loc);
					}
break;
case 23:
//#line 165 "Parser.y"
{
						yyval.fdef = new MethodDef(false, val_peek(4).ident, val_peek(5).type, val_peek(2).vlist, (Block) val_peek(0).stmt, val_peek(4).loc);
					}
break;
case 24:
//#line 171 "Parser.y"
{
						yyval.stmt = new Block(val_peek(1).slist, val_peek(2).loc);
					}
break;
case 25:
//#line 177 "Parser.y"
{
						yyval.slist.add(val_peek(0).stmt);
					}
break;
case 26:
//#line 181 "Parser.y"
{
                		yyval = new SemValue();
                		yyval.slist = new ArrayList<Tree>();
                	}
break;
case 27:
//#line 188 "Parser.y"
{
						yyval.stmt = val_peek(0).vdef;
					}
break;
case 28:
//#line 193 "Parser.y"
{
                		if (yyval.stmt == null) {
                			yyval.stmt = new Tree.Skip(val_peek(0).loc);
                		}
                	}
break;
case 38:
//#line 210 "Parser.y"
{
						yyval.stmt = new Tree.Assign(val_peek(2).lvalue, val_peek(0).expr, val_peek(1).loc);
					}
break;
case 39:
//#line 214 "Parser.y"
{
                		yyval.stmt = new Tree.Exec(val_peek(0).expr, val_peek(0).loc);
                	}
break;
case 40:
//#line 218 "Parser.y"
{
                		yyval = new SemValue();
                	}
break;
case 42:
//#line 225 "Parser.y"
{
                		yyval = new SemValue();
                	}
break;
case 43:
//#line 231 "Parser.y"
{
						yyval.lvalue = new Tree.Ident(val_peek(1).expr, val_peek(0).ident, val_peek(0).loc);
						if (val_peek(1).loc == null) {
							yyval.loc = val_peek(0).loc;
						}
					}
break;
case 44:
//#line 238 "Parser.y"
{
                		yyval.lvalue = new Tree.Indexed(val_peek(3).expr, val_peek(1).expr, val_peek(3).loc);
                	}
break;
case 45:
//#line 244 "Parser.y"
{
						yyval.expr = new Tree.CallExpr(val_peek(4).expr, val_peek(3).ident, val_peek(1).elist, val_peek(3).loc);
						if (val_peek(4).loc == null) {
							yyval.loc = val_peek(3).loc;
						}
					}
break;
case 46:
//#line 253 "Parser.y"
{
						yyval.expr = val_peek(0).lvalue;
					}
break;
case 49:
//#line 259 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.PLUS, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 50:
//#line 263 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.MINUS, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 51:
//#line 267 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.MUL, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 52:
//#line 271 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.DIV, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 53:
//#line 275 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.MOD, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 54:
//#line 279 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.EQ, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 55:
//#line 283 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.NE, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 56:
//#line 287 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.LT, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 57:
//#line 291 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.GT, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 58:
//#line 295 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.LE, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 59:
//#line 299 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.GE, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 60:
//#line 303 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.AND, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 61:
//#line 307 "Parser.y"
{
                		yyval.expr = new Tree.Binary(Tree.OR, val_peek(2).expr, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 62:
//#line 311 "Parser.y"
{
                		yyval = val_peek(1);
                	}
break;
case 63:
//#line 315 "Parser.y"
{
                		yyval.expr = new Tree.Unary(Tree.NEG, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 64:
//#line 319 "Parser.y"
{
                		yyval.expr = new Tree.Unary(Tree.NOT, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 65:
//#line 323 "Parser.y"
{
                		yyval.expr = new Tree.ReadIntExpr(val_peek(2).loc);
                	}
break;
case 66:
//#line 327 "Parser.y"
{
                		yyval.expr = new Tree.ReadLineExpr(val_peek(2).loc);
                	}
break;
case 67:
//#line 331 "Parser.y"
{
                		yyval.expr = new Tree.ThisExpr(val_peek(0).loc);
                	}
break;
case 68:
//#line 335 "Parser.y"
{
                		yyval.expr = new Tree.NewClass(val_peek(2).ident, val_peek(3).loc);
                	}
break;
case 69:
//#line 339 "Parser.y"
{
                		yyval.expr = new Tree.NewArray(val_peek(3).type, val_peek(1).expr, val_peek(4).loc);
                	}
break;
case 70:
//#line 343 "Parser.y"
{
                		yyval.expr = new Tree.TypeTest(val_peek(3).expr, val_peek(1).ident, val_peek(5).loc);
                	}
break;
case 71:
//#line 347 "Parser.y"
{
                		yyval.expr = new Tree.TypeCast(val_peek(2).ident, val_peek(0).expr, val_peek(0).loc);
                	}
break;
case 72:
//#line 351 "Parser.y"
{
                		yyval.expr = new Tree.Unary(Tree.POSTINC, val_peek(1).expr, val_peek(0).loc);
                	}
break;
case 73:
//#line 355 "Parser.y"
{
                		yyval.expr = new Tree.Unary(Tree.PREINC, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 74:
//#line 359 "Parser.y"
{
                		yyval.expr = new Tree.Unary(Tree.POSTDEC, val_peek(1).expr, val_peek(0).loc);
                	}
break;
case 75:
//#line 363 "Parser.y"
{
                		yyval.expr = new Tree.Unary(Tree.PREDEC, val_peek(0).expr, val_peek(1).loc);
                	}
break;
case 76:
//#line 367 "Parser.y"
{
                		yyval.expr = new Tree.Ternary(Tree.COND, val_peek(4).expr, val_peek(2).expr, val_peek(0).expr, val_peek(3).loc, val_peek(1).loc);
                	}
break;
case 77:
//#line 371 "Parser.y"
{
                		yyval.expr = new Tree.Numinstances(val_peek(1).ident, val_peek(3).loc);
                	}
break;
case 78:
//#line 377 "Parser.y"
{
						yyval.expr = new Tree.Literal(val_peek(0).typeTag, val_peek(0).literal, val_peek(0).loc);
					}
break;
case 79:
//#line 381 "Parser.y"
{
						yyval.expr = new Null(val_peek(0).loc);
					}
break;
case 81:
//#line 388 "Parser.y"
{
                		yyval = new SemValue();
                		yyval.elist = new ArrayList<Tree.Expr>();
                	}
break;
case 82:
//#line 395 "Parser.y"
{
						yyval.elist.add(val_peek(0).expr);
					}
break;
case 83:
//#line 399 "Parser.y"
{
                		yyval.elist = new ArrayList<Tree.Expr>();
						yyval.elist.add(val_peek(0).expr);
                	}
break;
case 84:
//#line 406 "Parser.y"
{
						yyval.stmt = new Tree.WhileLoop(val_peek(2).expr, val_peek(0).stmt, val_peek(4).loc);
					}
break;
case 85:
//#line 412 "Parser.y"
{
						yyval.stmt = new Tree.ForLoop(val_peek(6).stmt, val_peek(4).expr, val_peek(2).stmt, val_peek(0).stmt, val_peek(8).loc);
					}
break;
case 86:
//#line 418 "Parser.y"
{
						yyval.stmt = new Tree.Break(val_peek(0).loc);
					}
break;
case 87:
//#line 424 "Parser.y"
{
						yyval.stmt = new Tree.If(val_peek(3).expr, val_peek(1).stmt, val_peek(0).stmt, val_peek(5).loc);
					}
break;
case 88:
//#line 430 "Parser.y"
{
						yyval.stmt = val_peek(0).stmt;
					}
break;
case 89:
//#line 434 "Parser.y"
{
						yyval = new SemValue();
					}
break;
case 90:
//#line 440 "Parser.y"
{
						yyval.stmt = new Tree.Return(val_peek(0).expr, val_peek(1).loc);
					}
break;
case 91:
//#line 444 "Parser.y"
{
                		yyval.stmt = new Tree.Return(null, val_peek(0).loc);
                	}
break;
case 92:
//#line 450 "Parser.y"
{
						yyval.stmt = new Print(val_peek(1).elist, val_peek(3).loc);
					}
break;
case 93:
//#line 456 "Parser.y"
{
						yyval.elist.add(val_peek(2).expr);
						yyval.slist.add(val_peek(0).stmt);
					}
break;
case 94:
//#line 461 "Parser.y"
{
						yyval.elist = new ArrayList<Expr>();
						yyval.elist.add(val_peek(2).expr);
						yyval.slist = new ArrayList<Tree>();
						yyval.slist.add(val_peek(0).stmt);
					}
break;
case 95:
//#line 470 "Parser.y"
{
						yyval.stmt = new GuardedIf(val_peek(1).slist, val_peek(1).elist, val_peek(2).loc);
					}
break;
case 96:
//#line 476 "Parser.y"
{
						yyval.stmt = new GuardedDo(val_peek(1).slist, val_peek(1).elist, val_peek(2).loc);
					}
break;
//#line 1390 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    //if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      //if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        //if (yychar<0) yychar=0;  //clean, if necessary
        //if (yydebug)
          //yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      //if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
//## The -Jnorun option was used ##
//## end of method run() ########################################



//## Constructors ###############################################
//## The -Jnoconstruct option was used ##
//###############################################################



}
//################### END OF CLASS ##############################
