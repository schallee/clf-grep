package net.darkmist.clf;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.darkmist.alib.job.MoreExecutors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("unused")
public class Main implements FileHandler.Factory.Recycling
{
	private static final Class<Main> CLASS = Main.class;
	private static final String CLASS_NAME = CLASS.getName();
	private static final Log logger = LogFactory.getLog(CLASS);
	private static final TimeUnit STATUS_UNIT = TimeUnit.SECONDS;
	private static final long STATUS_TIME = 1;

	private LogBatchHandler chainDest;
	private Pattern usrPattern;
	private Pattern usrLinePattern;
	private String usr;
	private List<ThreadChain> threadChains = new LinkedList<ThreadChain>();

	private static class ThreadChain implements FileHandler
	{
		private /*Sorting*/Entries2Batch batcher;
		private FileHandler chainInput;

		/*
		ThreadChain(String usr, Pattern usrPattern, LogBatchHandler chainOutput)
		{
			LogHandler filterHandler;
			LogInputStreamHandler logStreamHandler;
			FileToStreamHandler file2Stream;
			FileHandler.Chained fileFilter;
	
			batcher = new SortingEntries2Batch(LogEntry.getDateOnlyComparator(), chainOutput);
			filterHandler = new UserRegexChainedLogHandler(usrPattern,batcher);
			logStreamHandler = new LogInputStreamHandler(filterHandler);
			file2Stream = new PossibleGzipFileToStreamHandler(logStreamHandler);
			fileFilter = new IndexedLogFileHandler(usrPattern, file2Stream);
			chainInput = fileFilter;
		}
		*/

		ThreadChain(Pattern usrLinePattern, Pattern usrPattern, LogBatchHandler chainOutput)
		{
			LogHandler filterLogHandler;
			Line2LogHandler line2LogHandler;
			RegexChainedLineHandler filterLineHandler;
			InputStream2LineHandler inputStream2LineHandler;
			FileToStreamHandler file2Stream;
			FileHandler.Chained fileFilter;
	
			batcher = new /*Sorting*/Entries2Batch(/*LogEntry.getDateOnlyComparator(),*/ chainOutput);
			filterLogHandler = new UserRegexChainedLogHandler(usrPattern,batcher);
			line2LogHandler = new Line2LogHandler(filterLogHandler);
			filterLineHandler = new RegexChainedLineHandler(usrLinePattern,line2LogHandler);
			inputStream2LineHandler = new InputStream2LineHandler(filterLineHandler);

			file2Stream = new PossibleGzipFileToStreamHandler(inputStream2LineHandler);
			fileFilter = new IndexedLogFileHandler(usrPattern, file2Stream);
			chainInput = fileFilter;
		}

		public void handleFile(File file)
		{
			chainInput.handleFile(file);
			batcher.flush();
		}
	}

	private static void usage(String msg)
	{
		System.err.println(msg);
		System.err.println("Usage: " + CLASS_NAME + " user output_file log_dir ...");
		System.exit(1);
	}

	private Main()
	{
	}

	public synchronized FileHandler makeFileHandler()
	{
		if(threadChains.size() > 0)
			return threadChains.remove(0);
		return new ThreadChain(usrLinePattern, usrPattern, chainDest);
	}

	public synchronized void recycleFileHandler(FileHandler handler)
	{
		if(!(handler instanceof ThreadChain))
		{
			logger.warn("recycled handler is not a ThreadChain but " + handler.getClass() + " ignoring...");
			return;
		}
		Util.checkedAdd(threadChains, (ThreadChain)handler);
	}

	private void handleFiles(String fileNames[], int off, int len)
	{
		DirTraverser traverser;
		Queue<File> files;
		ExecutorService executor;

		// convert fileNames to Files and put them in a Queue
		files = Util.newQueue(Util.getStringToFileConverter(), fileNames, off, len);
		//executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		executor = MoreExecutors.newCurrentThreadPool();

		traverser = new DirTraverser(files, new ExecutorFileHandler(executor, this));

		// let her rip
		traverser.run();

		// all done traversing... shutdown the executor
		executor.shutdown();
		// and wait for it
		while(!executor.isTerminated())
		{
			try
			{
				executor.awaitTermination(STATUS_TIME, STATUS_UNIT);
			}
			catch(InterruptedException e)
			{
				logger.warn("Ignoring InterruptedException until thread pool executor stops", e);
			}
			if(logger.isDebugEnabled() && executor instanceof ThreadPoolExecutor)
			{
				ThreadPoolExecutor pool = (ThreadPoolExecutor)executor;
				logger.debug("ThreadPool size=" + pool.getPoolSize() + " active=" + pool.getActiveCount() + " queue=" + pool.getQueue().size());
			}
		}
		executor=null;
		logger.debug("handleFiles done...");
	}

	private void implMain(String args[]) throws IOException
	{
		PrintWriter out;
		LogHandler printer;
		String usrRegex;
		String usrLineRegex;
		String outFile;
		//MergingBatchHandler batchMerger;
		SortingBatchCombiner batchMerger;

		if(args.length < 3)
			usage("Insufficient arguments");
		usr = args[0];
		outFile = args[1];

		// get and fixup regex
		usrRegex = usr;
		if(!usrRegex.startsWith(".*") && !usrRegex.startsWith("^"))
			usrRegex = ".*" + usrRegex;
		if(!usrRegex.endsWith(".*") && !usrRegex.endsWith("$"))
			usrRegex = usrRegex + ".*";
		usrPattern = Pattern.compile(usrRegex,Pattern.CASE_INSENSITIVE);

		usrLineRegex = 	"^[^ ]+ [^ ]+ [^ ]*" + usr + "[^ ]* .*";
		if(logger.isDebugEnabled())
			logger.debug("usrLineRegex=" + usrLineRegex + '=');
		usrLinePattern = Pattern.compile(usrLineRegex,Pattern.CASE_INSENSITIVE);

		if(outFile.equals("-"))
			out = new PrintWriter(System.out);
		else
			out = new PrintWriter(outFile);

		printer = new PrintingLogHandler(out);
		//batchMerger = new MergingBatchHandler(new Batch2Entries(printer), LogEntry.getDateOnlyComparator());
		batchMerger = new SortingBatchCombiner(new Batch2Entries(printer), LogEntry.getDateOnlyComparator());
		chainDest = LogBatchHandler.Utils.synchronizedLogBatchHandler(batchMerger);

		// figure the input and let it rip
		handleFiles(args,2,args.length-2);
		logger.debug("handleFiles is done...");

		// Nothing is output until here!
		logger.debug("flusing batchMerger...");
		batchMerger.flush();
		logger.debug("flusing out...");
		out.flush();
		logger.debug("done");
	}

	public static void main(String args[]) throws IOException
	{
		new  Main().implMain(args);
		logger.debug("done");
		System.exit(0);
	}
}
