package hazardland.java.sprite;

import hazardland.java.lib.tool.Text;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Main 
{
	public static final String file = "D:\\sataguri\\s3_sachuqrebi\\s3_papa.png";	
	
	public static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	public static String dir = "D:\\sataguri\\s3_sachuqrebi\\";
	public static File path;
	public static String home;

	public static void main(String[] args) 
	{
		write ("\nhazardland sprite console\n--------------------------");
//		BufferedImage image = load (file);
//		if (image!=null)
//		{
//			image.createGraphics().draw3DRect(0, 0, 40, 40, true);
//			image.createGraphics().draw3DRect(0, 80, 40, 40, true);
//			save (image, "d:\\test.png");
//			//image.
//
//		}
		home = path(System.getProperty("user.dir"));
		dir = hazardland.java.lib.tool.File.read (home+"/sprite.cfg");
		if (Text.empty(dir))
		{
			dir = home;
		}
		path = new File (dir);
		help ();
		pwd ();
		write("");
		//tiles (dir);
		while (true)
		{
			command (read (path.getName()+" > "));
		}
	}
	public static void command (String command)
	{
		command = command.trim();
		if (!Text.empty(command))
		{
			if (Text.empty(Text.before(" ", command)))
			{
				if (command.equalsIgnoreCase("pwd"))
				{
					pwd();
				}
				else if (command.equalsIgnoreCase("exit"))
				{
					exit();
				}
				else if (command.equalsIgnoreCase("help"))
				{
					help ();
				}
				else if (command.equalsIgnoreCase("ls"))
				{
					ls ();
				}
				else if (command.equalsIgnoreCase("make"))
				{
					make ();
				}
				else
				{
					write ("unknown command: "+command);
				}
			}
			else
			{
				String name = Text.before(" ", command);
				String value = Text.after(" ", command);
				if (name.equalsIgnoreCase("ls"))
				{
					ls (value);
				}
				else if (name.equalsIgnoreCase("cd"))
				{
					cd (value);
				}
				else if (name.equalsIgnoreCase("make"))
				{
					make (value);
				}
				else
				{
					write ("unknown command: "+name);
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
	public static ArrayList<BufferedImage> sprite (File[] files)
	{
		int width = 0;
		int height = 0;
		int count = 0;
		int last = 0;
		int position = 0;
		BufferedImage image = null;
		ArrayList<File> images = new ArrayList<File>();
		for (File item: files)
		{
			if (valid(item))
			{
				last = position;
				image = load (files[last]);
				write ("loaded "+item.getName()+" "+image.getWidth()+"x"+image.getHeight());
				if (image==null)
				{
					write ("sprite: can't open last file");
					return null;
				}
				if (width!=0 && width!=image.getWidth() && height!=0 && height!=image.getHeight())
				{
					write ("sprite: error not all images are with the same height and width in folder "+item.getParentFile().getName());
					return null;
				}
				width = image.getWidth();
				height = image.getHeight();		
				count++;
				images.add(item);
			}
			position++;
		}
		write ("sprite: found "+count+" files with "+width+"x"+height);
		
		int horizontal = 1;
		int vertical = count;
		
		while (width*(horizontal+1)<=1024 && horizontal<count)
		{
			horizontal++;
		}
		vertical = count/horizontal;
		if (vertical==0)
		{
			vertical = 1;
		}
		if (vertical*horizontal!=count)
		{
			vertical++; 
		}
		write ("horizontal is "+horizontal+" vertical is "+vertical+" with count "+count);
		
		ArrayList<BufferedImage> result = new ArrayList<BufferedImage>();
		int cell = 0;
		int row = 0;
		BufferedImage part = null;
		int fit = 0;
		int empty = (horizontal*vertical)-count;
		for (File file: images)
		{
			image = load (file);
			if (image==null)
			{
				return null;
			}
			if (part==null || fit==0)
			{
				fit = 1;
				if (height*vertical>1024)
				{
					write ("height is big "+(height*vertical));
					while (height*(fit+1)<=1024 && fit<vertical)
					{
						fit++;
					}
					if (vertical==1)
					{
						horizontal = count;
						write ("triming horizontal to "+horizontal);
					}					
					write ("fit is "+fit);
				}
				else
				{
					write ("empty is "+empty);
					if (vertical==1)
					{
						horizontal = count;
						write ("triming horizontal to "+horizontal);
					}
					else
					{
						write (count+"<"+"("+horizontal+"-1)*"+vertical);
						while (empty>0 && vertical-1<=empty && count<=(horizontal-1)*vertical)
						{
							horizontal -= 1;
							empty -= (vertical-1); 
						}
					}
					write ("optimized horizontal is "+horizontal+" vertical is "+vertical+" with count "+count);					
					fit = vertical;
				}
				part = new BufferedImage(width*horizontal, height*fit, BufferedImage.TYPE_INT_ARGB);
				result.add (part);
			}
			part.createGraphics().drawImage(image, cell*width, row*height, null);
			cell++;
			if (cell==horizontal)
			{
				write ("filled line");
				row++;
				cell = 0;
			}
			if (row>=fit)
			{
				row = 0;
				write ("old count is "+count+ " ("+(horizontal*fit)+")");
				count -= (horizontal*fit);
				write ("new count is "+count);
				vertical -= fit;
				fit = 0;
			}
		}
		write ("done");
		return result;
	}
	
	
//	private int near (int input)
//	{
//		int result = 2;
//		while (result<input)
//		{
//			result *= 2;
//		}
//		return result;
//	}

	public static boolean valid (File image)
	{
		if (!image.isDirectory() && Text.afterLast(".", image.getName()).equalsIgnoreCase("png"))
		{
			return true;
		}
		return false;
	}
	
	
	public static void make ()
	{
		make (null);
	}
	
	public static void build (File folder)
	{
		build (folder, false);
	}
	
	public static void build (File folder, boolean confirm)
	{
		String output = null;
		if (folder.getParent()!=null)
		{
			try 
			{
				output = path(folder.getParentFile().getCanonicalPath());
			} 
			catch (IOException e) 
			{
				write ("build: can't get parent folder");
				return;
			}
			if (output.charAt(output.length()-1)!='/')
			{
				output = output + '/';
			}
			else
			{
				write ("build: last char "+output.charAt(output.length()-1));
			}
			output += folder.getName();
		}
		else
		{
			write ("build: parent directory does not exist for "+folder.getParent());
			return;
		}
		File[] list = folder.listFiles();
		if (list!=null)
		{
			String status = "build: building "+output+".png => with";
			boolean found = false;
			for (File image: list)
			{
				if (valid(image))
				{
					found = true;
					status += " " + image.getName();
				}
			}
			write (status);
			boolean build = true;
			if (confirm && found)
			{
				build = false;
				String read = read ("build: continiue ? [Y/n] ");
				if (Text.empty(read) || read.equalsIgnoreCase("y"))
				{
					build = true;
				}
			}
			if (build && found)
			{
				write ("build: entering "+output);
				ArrayList<BufferedImage> result = sprite (list);
				if (result==null)
				{
					write ("build: failed");
				}
				else
				{
					if (result.size()==1)
					{
						save (result.get(0), output+".png");
						write ("build: created file "+output+".png");
					}
					else if (result.size()>1)
					{
						int position = 0;
						for (BufferedImage image: result)
						{
							position++;
							save (image, output+"_"+position+".png");
							write ("build: created file "+output+"_"+position+".png");
						}
					}
				}
			}
			else
			{
				write ("build: nothing to do with "+folder.getAbsolutePath());
			}			
		}
		else
		{
			write ("build: emtpy folder "+path(folder.getAbsolutePath()));

		}
	}
	
	public static void make (String path)
	{
		boolean confirm = false;
		if (path==null)
		{
			confirm = true;
			path = dir;
		}		
		if (!path.equalsIgnoreCase("all"))
		{
			File folder = folder (path);
			if (folder.exists())
			{
				build (folder, confirm);
			}
			else
			{
				write ("make: invalid path "+path(folder.getAbsolutePath()));
			}
		}
		else
		{
			if (Main.path.exists())
			{
				File[] list = Main.path.listFiles();
				if (list!=null)
				{
					int found = 0;
					for (File sprite: list)
					{
						if (sprite.isDirectory() && !sprite.getAbsolutePath().equals(Main.path.getAbsolutePath()))
						{
							build (sprite);
							found++;
						}
					}
					if (found==0)
					{
						write ("make: folders not found, use 'make' instead of 'make all' to build current folder");
					}
					
				}
				else
				{
					write ("make: folder is empty");
				}
			}
			else
			{
				write ("make: invalid path "+path(Main.path.getAbsolutePath()));
			}			
		}
	}
	public static void pwd ()
	{
		write ("pwd: current folder is "+dir);
	}
	public static void exit ()
	{
		write ("bye !");		
		System.exit (0);
	}
	public static void cd (String path)
	{
		path = path.trim();
		if (!Text.empty(path))
		{
			path = path (path);
			if (path.length()>=2 && path.charAt(0)=='.' && path.charAt(1)=='.')
			{
				if (path.length()==3)
				{
					if (dir.charAt(path.length()-1)=='/')
					{
						path = Text.beforeLast("/", dir);
					}
					path = Text.beforeLast("/", dir);
					write ("debug: "+path);
				}
				else
				{
					write ("cd: currently '../[path]' not supported by cd");
					return;
				}
			}
			else if (path.length()>=1 && path.charAt(0)=='.' && path.charAt(1)=='/')
			{
				path = Text.after("./", path);
			}
			if ((path.length()==1 && path.charAt(0)=='.') || Text.empty(path))
			{
				write ("cd: invalid input");
			}
			File location = folder (path);
			if (location!=null && location.exists())
			{
				if (location.isDirectory())
				{
					dir = path(location.getAbsolutePath());
					Main.path = location;
					hazardland.java.lib.tool.File.write (home+"/sprite.cfg", dir);
				}
				else
				{
					write ("cd: path is not folder");
				}
			}
			else
			{
				write ("cd: invalid path");
			}
		}
		else
		{
			write ("cd: input emtpy");
		}
		pwd ();			
	}
	public static String path (String path)
	{
		return path.trim().replace("\\", "/");
	}
	public static boolean exists (String path)
	{
		File target = new File (path);
		if (target.exists())
		{
			return true;
		}
		return false;
	}
	public static void ls ()
	{
		ls (dir);
	}
	public static void ls (String path)
	{
		File[] list = list (path);
		if (list!=null)
		{
			for (File item: list)
			{
				if (item.isDirectory())
				{
					write ("<dir> "+item.getName());
				}
				else
				{
					write (item.getName());
				}
			}
			write ("");
		}
		else
		{
			write ("list: folder does not exist "+path);
		}
	}
	public static void help ()
	{
		write ("help - display help\n" +
			   "pwd - display current directory\n" +
			   "ls [path] - list files in directory\n" +
			   "cd path - change directory\n" +
			   "make [path] [all] - make sprites from folder(s)\n" +
			   "exit - exit console\n");
	}
	public static File folder (String path)
	{
		path = path (path);
		if (!Text.empty(path))
		{
			if ((path.length()>=2 && path.charAt(1)==':') || path.charAt(0)=='/')
			{
				return new File (path);
			}
			else
			{
				return new File (Main.path, path);
			}
		}
		return null;
	}
	public static File[] list (String path)
	{
		File folder = folder (path);
		File[] list = folder.listFiles();
		if (list!=null)
		{
			Arrays.sort (list);
		}
		return list;
		
	}
	
	public static BufferedImage load (String path)
	{
		return load (new File(path));
	}
	
	public static BufferedImage load (File file)
	{
		BufferedImage image = null;
		try 
		{
			image = ImageIO.read (file);
			//write("loaded "+file.getName()+" "+image.getWidth()+"x"+image.getHeight());
		} 
		catch (IOException e)
		{
			write ("cant read "+path);
		}
		return image;
	}
	
	public static boolean save (BufferedImage image, String path)
	{
		try 
		{
		    File file = new File (path);
		    ImageIO.write(image, Text.afterLast(".", path), file);
		    //write ("saved file "+path);
		    return true;
		} 
		catch (IOException e) 
		{
		    write ("cant save file");
		}
		return false;
	}
	public static void write (String string)
	{
		System.out.println (string);
	}
	
	public static String read ()
	{
		return read (null);
	}
	
	public static String read (String print)
	{
		String result = null;
		if (print!=null)
		{
			System.out.print (print);
		}
		try 
		{
		    result = input.readLine();
		} 
		catch (IOException e) 
		{
		
		}
		return result;
	}	
}
