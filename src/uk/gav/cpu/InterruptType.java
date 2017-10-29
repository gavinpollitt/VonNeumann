package uk.gav.cpu;

public enum InterruptType {
	PRE_FETCH,
	PRE_DECODE,
	PRE_EXECUTE,
	PRE_ADD_BUS,
	PRE_DATA_BUS,
	POST_FETCH,
	POST_DECODE,
	POST_EXECUTE,
	POST_CYCLE,
	PRE_MAR,
	PRE_MDR,
	POST_MAR,
	POST_MDR,
	POST_PC,
	POST_EXIT,
	POST_DATA_BUS,
	POST_COMMAND;
}