package net.sf.regadb.util.reflection;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;

public class Package
{
	private static Package instance_ = null;

	private Package()
	{

	}

	public static Package getInstance()
	{
		if (instance_ != null)
		{
			instance_ = new Package();
		}

		return instance_;
	}

	/**
	 * list Classes inside a given package
	 * 
	 * @param pckgname
	 *            String name of a Package, EG "java.lang"
	 * @return Class[] classes inside the root of the given package
	 * @throws ClassNotFoundException
	 *             if the Package is invalid
	 */
	public static Class[] getClasses(String pckgname) throws ClassNotFoundException
	{
		ArrayList classes = new ArrayList();
		File directory = null;
		try
		{
			URL packageURL = Thread.currentThread().getContextClassLoader().getResource(pckgname.replace('.', '/'));
			// we use this deprecated method because this also works from within
			// eclipse
			directory = new File(URLDecoder.decode(packageURL.getFile()));
		}
		catch (NullPointerException x)
		{
			throw new ClassNotFoundException(pckgname + " does not appear to be a valid package");
		}
		if (directory.exists())
		{
			String[] files = directory.list();
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].endsWith(".class"))
				{
					classes.add(Class.forName(pckgname + '.' + files[i].substring(0, files[i].length() - 6)));
				}
			}
		}
		else
		{
			throw new ClassNotFoundException(pckgname + " does not appear to be a valid package");
		}
		Class[] classesA = new Class[classes.size()];
		classes.toArray(classesA);
		return classesA;
	}
}
