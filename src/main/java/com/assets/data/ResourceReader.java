package com.assets.data;

import java.io.File;

import com.assets.ByteReader;

public class ResourceReader {
	private boolean needSearch;
	private String path;
	private SerializedFile assetsFile;
	private long offset;
	private long size;
	private ByteReader reader;

	public ResourceReader(String path, SerializedFile assetsFile, long offset, long size) {
		needSearch = true;
		this.path = path;
		this.assetsFile = assetsFile;
		this.offset = offset;
		this.size = size;
	}

	public ResourceReader(ByteReader reader, long offset, long size) {
		this.reader = reader;
		this.offset = offset;
		this.size = size;
	}

	private ByteReader GetReader()
    {
        if (needSearch)
        {
            var resourceFileName = new File(path).getName();
            if (assetsFile.assetsManager.resourceFileReaders.TryGetValue(resourceFileName, out reader))
            {
                needSearch = false;
                return reader;
            }
            var assetsFileDirectory = Path.GetDirectoryName(assetsFile.fullName);
            var resourceFilePath = Path.Combine(assetsFileDirectory, resourceFileName);
            if (!File.Exists(resourceFilePath))
            {
                var findFiles = Directory.GetFiles(assetsFileDirectory, resourceFileName, SearchOption.AllDirectories);
                if (findFiles.Length > 0)
                {
                    resourceFilePath = findFiles[0];
                }
            }
            if (File.Exists(resourceFilePath))
            {
                needSearch = false;
                reader = new BinaryReader(File.OpenRead(resourceFilePath));
                assetsFile.assetsManager.resourceFileReaders.Add(resourceFileName, reader);
                return reader;
            }
            throw new FileNotFoundException($"Can't find the resource file {resourceFileName}");
        }
        else
        {
            return reader;
        }
    }

	public byte[] GetData() {
		var binaryReader = GetReader();
		binaryReader.BaseStream.Position = offset;
		return binaryReader.ReadBytes((int) size);
	}

	public void GetData(byte[] buff) {
		var binaryReader = GetReader();
		binaryReader.BaseStream.Position = offset;
		binaryReader.Read(buff, 0, (int) size);
	}

	public void WriteData(string path)
    {
        var binaryReader = GetReader();
        binaryReader.BaseStream.Position = offset;
        using (var writer = File.OpenWrite(path))
        {
            binaryReader.BaseStream.CopyTo(writer, size);
        }
    }
}