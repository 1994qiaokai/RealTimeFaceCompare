package com.hzgc.ftpserver.address;

import com.hzgc.dubbo.address.FtpAddressService;
import com.hzgc.util.FileUtil;
import com.hzgc.util.IOUtil;

import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Properties;

public class FtpAddressServiceImpl implements FtpAddressService, Serializable {
    private static Properties proper = new Properties();

    public FtpAddressServiceImpl() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(FileUtil.loadResourceFile("ftpAddress.properties"));
            proper.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeStream(fis);
        }
    }

    @Override
    public Properties getFtpAddress() {
        return proper;
    }
}
