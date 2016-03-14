import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Exercise19
{
    // Not done!!

	/*
	- Change the letter counter to count the occurrences of every letter in the text, using a Map.
	- For example, for the text "hello", the resulting map should contain:
		"h" -> 1
		"e" -> 1
		"l" -> 2
		"o" -> 1
	*/
    public static void main(String[] args)
    {
        Exercise18.run();
    }
    public static HashMap<Character, Integer> map1 = new HashMap<>();

    private static int letterCount( String str, char ch, int start, int end )
    {

        for( int i = start; i < end; i++ ) {
            if ( Character.isLetter( str.charAt(i) ) )
            {
                if ( map1.containsKey( str.charAt( i ) ) )
                {
                    map1.put( str.charAt( i ),map1.get( str.charAt(i)) + 1 );
                } else
                {
                    map1.put( str.charAt( i ),1 );
                }
            }
        }
        return 0;
    }

    public static void run()
    {
        // Contains bugs!
        try {
            File file = new File( "F:\\OneDrive\\Studie\\Datalogi 2016\\Concurrent Programming\\CP Week10\\src\\Dummytext2.txt" );
            // File file = new File( "test.txt" );
            FileInputStream is = new FileInputStream( file );
            final int size = (int)file.length();
            byte[] content = new byte[ size ];
            is.read( content );
            final String str = new String( content );
            doThat( "Sequential one slice", () -> System.out.println( letterCount( str, 'e', 0, size ) ) );
            doThat( "Sequential two slices", () -> {
                int c1 = letterCount( str, 'e', 0, size/2 );
                int c2 = letterCount( str, 'e', size/2 + 1, size );
                System.out.println( c1 + c2 );
            } );
            doThat( "Multithreaded two slices", () -> {
                final AtomicInteger result = new AtomicInteger( 0 );
                Thread t1 = new Thread( () -> {
                    result.addAndGet( letterCount( str, 'e', 0, size/2 ) );
                } );
                Thread t2 = new Thread( () -> {
                    result.addAndGet( letterCount( str, 'e', size/2 + 1, size ) );
                } );
                t1.start();
                t2.start();
                try {
                    t1.join();
                    t2.join();
                } catch( InterruptedException e ) {}
                System.out.println( result.get() );
            } );
            doThat( "Multithreaded three slices", () -> {
                final AtomicInteger result = new AtomicInteger( 0 );
                Thread t1 = new Thread( () -> {
                    result.addAndGet( letterCount( str, 'e', 0, size/3 ) );
                } );
                Thread t2 = new Thread( () -> {
                    result.addAndGet( letterCount( str, 'e', size/3 + 1, (size/3)*2 ) );
                } );
                Thread t3 = new Thread( () -> {
                    result.addAndGet( letterCount( str, 'e', (size/3)*2 + 1, size ) );
                } );
                t1.start();
                t2.start();
                t3.start();
                try {
                    t1.join();
                    t2.join();
                    t3.join();
                } catch( InterruptedException e ) {}
                System.out.println( result.get() );
            } );
            doThat( "Multithreaded N slices", () -> {
                final int numCores = 5;
                final AtomicInteger result = new AtomicInteger( 0 );
                final Thread[] tt = new Thread[ numCores ];

                for( int i = 0; i < numCores; i++ ) {
                    final int threadIndex = i;
                    if ( i != numCores - 1 ) {
                        tt[i] = new Thread( () -> {
                            result.addAndGet(
                                    letterCount(
                                            str, 'e', size/numCores * threadIndex,
                                            ((size/numCores)*(threadIndex + 1)) - 1 ) );
                        } );
                    } else {
                        tt[i] = new Thread( () -> {
                            result.addAndGet(
                                    letterCount(
                                            str, 'e', size/numCores * threadIndex, size - 1 ) );
                        } );
                    }

                }
                for( int i = 0; i < numCores; i++ ) {
                    tt[i].start();
                }
                try {
                    for( int i = 0; i < numCores; i++ ) {
                        tt[i].join();
                    }
                } catch( InterruptedException e ) {}
                System.out.println( result.get() );
            } );
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    private static void doThat( String caption, Runnable runnable )
    {
        long tStart = System.currentTimeMillis();
        runnable.run();
        System.out.println( caption + " took " + (System.currentTimeMillis() - tStart) + "ms" );
    }
}