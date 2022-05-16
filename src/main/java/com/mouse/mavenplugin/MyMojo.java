package com.mouse.mavenplugin;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.function.Function;

/**
 * @author yyzsx
 * @version 1.0.0
 * @ProjectName yyzsx-maven-plugin
 * @ClassName MyMojo.java
 * @Description 演示-输出项目信息
 * @createTime 2021年08月08日 13:53:00
 */
@Mojo(name = "showProjectInfo", defaultPhase = LifecyclePhase.COMPILE)
public class MyMojo extends AbstractMojo {
    /**
     * 项目根路径
     */
    @Parameter(defaultValue = "${basedir}", readonly = true, required = true)
    private File basedir;

    /**
     * 获取pom文件中项目版本，或者支持-Dversion=xx，进行传入
     */
    @Parameter(property = "version", defaultValue = "${project.version}", readonly = true, required = true)
    private String version;

    /**
     * 获取本地maven仓库地址
     */
    @Parameter(defaultValue = "${settings.localRepository}", readonly = true, required = true)
    private String localRepository;

    /**
     * 参数配置，作者
     */
    @Parameter(property = "author", defaultValue = "${showProjectInfo.author}", readonly = true, required = true)
    private String author;

    /***
     * 参数集合
     */
    @Parameter(property = "lists", readonly = true)
    private List lists;

    /**
     * Dependency injected
     */
    @Parameter(defaultValue = "${project}")
    public MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("*******************ShowProjectInfo*******************");
        getLog().info("--------------------MyFirstMojo,begin--------------------");
        System.out.println("欢迎语：Hello Maven Plugin");
        System.out.println("项目本地仓库localRepository:" + this.localRepository);
        System.out.println("项目跟路径basedir:" + basedir);
        System.out.println("项目版本version:" + this.version);
        System.out.println("项目作者author:" + author);
        System.out.println("项目包含文件类型lists：" + StringUtils.join(lists.toArray(), ","));
        // recursive read files in basedir
        listFilesForFolder(new File(basedir.getPath() + "/target/classes"), (file) -> {
            System.out.println(file.getName());
            if (file.getPath().contains(".class")) {
                String className = file.getPath().replaceFirst(".*com", "com").replaceFirst(".class", "").replace("/", ".");
                getLog().info("className="+ className);
                try {
                    Class clazz = getClassLoader(project).loadClass(className);
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field f : fields) {
                        getLog().info("Field=" + f.getName() + "isPrivate=" + Modifier.isPrivate(f.getModifiers()));
                    }
                    Method[] methods = clazz.getDeclaredMethods();
                    for (Method m : methods) {
                        getLog().info("method=" + m.getName() + "isPrivate=" + Modifier.isPrivate(m.getModifiers()));
                    }
                    getLog().info("class=" + clazz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return "";
        });

        getLog().info("--------------------MyFirstMojo,end-----------------------");
    }

    private ClassLoader getClassLoader(MavenProject project)
    {
        try
        {
            List classpathElements = project.getCompileClasspathElements();
            classpathElements.add( project.getBuild().getOutputDirectory() );
            classpathElements.add( project.getBuild().getTestOutputDirectory() );
            URL urls[] = new URL[classpathElements.size()];
            for ( int i = 0; i < classpathElements.size(); ++i )
            {
                urls[i] = new File( (String) classpathElements.get( i ) ).toURL();
            }
            return new URLClassLoader( urls, this.getClass().getClassLoader() );
        }
        catch ( Exception e )
        {
            getLog().debug( "Couldn't get the classloader." );
            return this.getClass().getClassLoader();
        }
    }

    public void listFilesForFolder(final File folder, Function<File, String> function) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry, function);
            } else {
                System.out.println(fileEntry.getName());
                function.apply(fileEntry);
            }
        }
    }
}
 