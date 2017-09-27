/**
 * 
 */
package io.sinistral.proteus.server;

import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.restassured.RestAssured;
import io.sinistral.proteus.ProteusApplication;
import io.sinistral.proteus.controllers.Tests;
import io.sinistral.proteus.services.AssetsService;
import io.sinistral.proteus.services.SwaggerService;

/**
 * @author jbauer
 */
public class DefaultServer extends BlockJUnit4ClassRunner
{
	private static Logger log = LoggerFactory.getLogger(DefaultServer.class.getCanonicalName());

	private static boolean first = true;

	/**
	 * @param clazz
	 * @throws InitializationError
	 */
	public DefaultServer(Class<?> clazz) throws InitializationError
	{
		super(clazz); 
	}

	@Override
	public void run(final RunNotifier notifier)
	{
		notifier.addListener(new RunListener()
		{
			@Override
			public void testStarted(Description description) throws Exception
			{

				super.testStarted(description);
			}

			@Override
			public void testFinished(Description description) throws Exception
			{

				super.testFinished(description);
			}
		});

		runInternal(notifier);

		super.run(notifier);
	}

	private static void runInternal(final RunNotifier notifier)
	{
	 
		if (first)
		{  
			
			first = false;
			
			final ProteusApplication app = new ProteusApplication();
			 
			app.addService(SwaggerService.class);
			app.addService(AssetsService.class);
			app.addController(Tests.class);

			app.start();
			
			int port = 0;
			 
			try
			{
				Thread.sleep(2000);
				
				List<Integer> ports = app.getPorts();
				
				port = ports.get(0);
				
			} catch (Exception e)
			{
				 log.error(e.getMessage(),e);
			}
		 
			 
 			
			RestAssured.baseURI = String.format("http://localhost:%d/v1",port);
			
			RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
			
			while (!app.isRunning())
			{
				try
				{
					Thread.sleep(100L);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			notifier.addListener(new RunListener()
			{
				@Override
				public void testRunFinished(final Result result) throws Exception
				{
					app.shutdown();
				};
			});
		}

	}

}
