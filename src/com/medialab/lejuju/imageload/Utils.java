/**
 * Copyright (c) 2012-2013, Michael Yang 杨福海 (www.yangfuhai.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.medialab.lejuju.imageload;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class Utils {
	
	private static final String TAG = "BitmapCommonUtils";
	private static final long POLY64REV = 0x95AC9329AC4BC9B5L;
    private static final long INITIALCRC = 0xFFFFFFFFFFFFFFFFL;

    private static long[] sCrcTable = new long[256];
	/**
	 * 获取可以使用的缓存目录
	 * @param context
	 * @param uniqueName 目录名称
	 * @return
	 */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ? 
                		getExternalCacheDir(context).getPath() : context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

  

    /**
     * 获取bitmap的字节大小
     * @param bitmap
     * @return
     */
    public static int getBitmapSize(Bitmap bitmap) {
        return bitmap.getRowBytes() * bitmap.getHeight();
    }


   /**
    * 获取程序外部的缓存目录
    * @param context
    * @return
    */
    public static File getExternalCacheDir(Context context) {
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    /**
     * 获取文件路径空间大小
     * @param path
     * @return
     */
    public static long getUsableSpace(File path) {
    	try{
    		 final StatFs stats = new StatFs(path.getPath());
    	     return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    	}catch (Exception e) {
			Log.e(TAG, "获取 sdcard 缓存大小 出错，请查看AndroidManifest.xml 是否添加了sdcard的访问权限");
			e.printStackTrace();
			return -1;
		}
       
    }
    
    
    public static byte[] getBytes(String in) {
        byte[] result = new byte[in.length() * 2];
        int output = 0;
        for (char ch : in.toCharArray()) {
            result[output++] = (byte) (ch & 0xFF);
            result[output++] = (byte) (ch >> 8);
        }
        return result;
    }

    public static boolean isSameKey(byte[] key, byte[] buffer) {
        int n = key.length;
        if (buffer.length < n) {
            return false;
        }
        for (int i = 0; i < n; ++i) {
            if (key[i] != buffer[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static byte[] copyOfRange(byte[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        byte[] copy = new byte[newLength];
        System.arraycopy(original, from, copy, 0,Math.min(original.length - from, newLength));
        return copy;
    }
    
    
    
    static {
        //参考 http://bioinf.cs.ucl.ac.uk/downloads/crc64/crc64.c
        long part;
        for (int i = 0; i < 256; i++) {
            part = i;
            for (int j = 0; j < 8; j++) {
                long x = ((int) part & 1) != 0 ? POLY64REV : 0;
                part = (part >> 1) ^ x;
            }
            sCrcTable[i] = part;
        }
    }
    
    public static byte[] makeKey(String httpUrl) {
        return getBytes(httpUrl);
    }

    /**
     * A function thats returns a 64-bit crc for string
     *
     * @param in input string
     * @return a 64-bit crc value
     */
    public static final long crc64Long(String in) {
        if (in == null || in.length() == 0) {
            return 0;
        }
        return crc64Long(getBytes(in));
    }

    public static final long crc64Long(byte[] buffer) {
        long crc = INITIALCRC;
        for (int k = 0, n = buffer.length; k < n; ++k) {
            crc = sCrcTable[(((int) crc) ^ buffer[k]) & 0xff] ^ (crc >> 8);
        }
        return crc;
    }
    
    private static boolean[] dontNeedEncoding;

	static
	{
		dontNeedEncoding = new boolean[256];

		for (int i = 0; i < 256; i++)
		{
			boolean b = ((i >= '0') && (i <= '9')) || ((i >= 'A') && (i <= 'Z')) || ((i >= 'a') && (i <= 'z'));

			dontNeedEncoding[i] = b;
		}

		dontNeedEncoding[' '] = true;
		dontNeedEncoding['-'] = true;
		dontNeedEncoding['_'] = true;
		dontNeedEncoding['.'] = true;
		dontNeedEncoding['*'] = true;
	}
	
	public static String encode(String s)
	{
		if (s == null) return null;

		boolean wroteUnencodedChar = false;

		StringBuffer writer = new StringBuffer();

		StringBuffer out = new StringBuffer(s.length());

		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);

			if ((c < 256) && dontNeedEncoding[c])
			{
				if (c == ' ')
				{
					c = '+';
				}

				out.append((char)c);
				wroteUnencodedChar = true;
			}
			else
			{
				try
				{
					if (wroteUnencodedChar)
					{
						writer = new StringBuffer();
						wroteUnencodedChar = false;
					}

					writer.append(c);

					if (c >= 0xD800 && c <= 0xDBFF)
					{
						if ((i + 1) < s.length())
						{
							int d = (int)(s.charAt(i + 1));

							if (d >= 0xDC00 && d <= 0xDFFF)
							{
								writer.append(d);
								i++;
							}
						}
					}

				}
				catch (Exception e)
				{
					writer = new StringBuffer();
					continue;
				}

				String str = writer.toString();

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(baos);
				try
				{
					dos.writeUTF(str);
					dos.flush();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				byte[] temp = baos.toByteArray();
				byte[] ba = new byte[temp.length - 2];
				for (int ix = 0; ix < ba.length; ix++)
				{
					ba[ix] = temp[ix + 2];
				}

				for (int j = 0; j < ba.length; j++)
				{
					out.append('%');

					char ch = forDigit((ba[j] >> 4) & 0xF, 16);
					out.append(ch);

					ch = forDigit(ba[j] & 0xF, 16);
					out.append(ch);
				}

				writer = new StringBuffer();
				try
				{
					dos.close();
					baos.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		return out.toString();
	}
	
	private static char forDigit(int digit, int radix)
	{
		if ((digit >= radix) || (digit < 0))
		{
			return '0';
		}
		if (digit < 10)
		{
			return (char)('0' + digit);
		}
		return (char)('A' + digit - 10);
	}
	
	/**
     * 柔化效果(高斯模糊)
     * @param bmp
     * @return
     */ 
    private Bitmap blurImageAmeliorate(Bitmap bmp) 
    { 
        long start = System.currentTimeMillis(); 
        // 高斯矩阵 
        int[] gauss = new int[] { 1, 2, 1, 2, 4, 2, 1, 2, 1 }; 
         
        int width = bmp.getWidth(); 
        int height = bmp.getHeight(); 
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565); 
         
        int pixR = 0; 
        int pixG = 0; 
        int pixB = 0; 
         
        int pixColor = 0; 
         
        int newR = 0; 
        int newG = 0; 
        int newB = 0; 
         
        int delta = 16; // 值越小图片会越亮，越大则越暗 
         
        int idx = 0; 
        int[] pixels = new int[width * height]; 
        bmp.getPixels(pixels, 0, width, 0, 0, width, height); 
        for (int i = 1, length = height - 1; i < length; i++) 
        { 
            for (int k = 1, len = width - 1; k < len; k++) 
            { 
                idx = 0; 
                for (int m = -1; m <= 1; m++) 
                { 
                    for (int n = -1; n <= 1; n++) 
                    { 
                        pixColor = pixels[(i + m) * width + k + n]; 
                        pixR = Color.red(pixColor); 
                        pixG = Color.green(pixColor); 
                        pixB = Color.blue(pixColor); 
                         
                        newR = newR + (int) (pixR * gauss[idx]); 
                        newG = newG + (int) (pixG * gauss[idx]); 
                        newB = newB + (int) (pixB * gauss[idx]); 
                        idx++; 
                    } 
                } 
                 
                newR /= delta; 
                newG /= delta; 
                newB /= delta; 
                 
                newR = Math.min(255, Math.max(0, newR)); 
                newG = Math.min(255, Math.max(0, newG)); 
                newB = Math.min(255, Math.max(0, newB)); 
                 
                pixels[i * width + k] = Color.argb(255, newR, newG, newB); 
                 
                newR = 0; 
                newG = 0; 
                newB = 0; 
            } 
        } 
         
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height); 
        long end = System.currentTimeMillis(); 
        Log.d("may", "used time="+(end - start)); 
        return bitmap; 
    }  

}
