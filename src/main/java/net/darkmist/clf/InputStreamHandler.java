package net.darkmist.clf;

import java.io.InputStream;
import java.io.IOException;

public interface InputStreamHandler
{
	public void handleInputStream(InputStream in) throws IOException;

	public interface Factory
	{
		public InputStreamHandler makeInputStreamHandler();

		public interface Recycling extends Factory
		{
			public void recycleInputStreamHandler(InputStreamHandler handler);
		}
	}
}
