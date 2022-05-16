package com.mouse.mavenplugin;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.List;

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
        getLog().info("--------------------MyFirstMojo,end-----------------------");
    }
}
 