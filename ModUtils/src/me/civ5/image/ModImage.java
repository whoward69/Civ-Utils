package me.civ5.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.jnlp.FileContents;

import me.civ5.dds.imageio.DDSImageReaderSpi;

public class ModImage {
	static {
		IIORegistry.getDefaultInstance().registerServiceProvider(new DDSImageReaderSpi());
	}

	private String path;
	private String name;
	private BufferedImage data;
	
	public ModImage(FileContents file) throws IOException {
		this(null, file.getName(), ImageIO.read(file.getInputStream()));
	}
	
	public ModImage(File file) throws IOException {
		this(file.getParent(), file.getName(), ImageIO.read(new FileInputStream(file)));
	}

	public ModImage(String path, String name, BufferedImage data) {
		this.path = path;
		this.name = name;
		this.data = data;
	}

	public String getPath() {
		return path;
	}

	public String getName() {
		return name;
	}
	
	public int getWidth() {
		return data.getWidth();
	}
	
	public int getHeight() {
		return data.getHeight();
	}

	public BufferedImage getData() {
		return data;
	}
}
