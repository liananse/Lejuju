/*-
 * Copyright (C) 2010 Google Inc.
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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ContentHandler;
import java.net.URLConnection;


/**
 * A {@link ContentHandler} that decodes a {@link Bitmap} from a
 * {@link URLConnection}.
 * <p>
 * The implementation includes a work-around for <a
 * href="http://code.google.com/p/android/issues/detail?id=6066">Issue 6066</a>.
 * <p>
 * An {@link IOException} is thrown if there is a decoding exception.
 */
public class BitmapContentHandler extends ContentHandler {
	
    @Override
    public byte[] getContent(URLConnection connection) throws IOException {
        InputStream input = connection.getInputStream();
        try {
            input = new BlockingFilterInputStream(input);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = input.read(b, 0, 1024)) != -1) 
            {
	             baos.write(b, 0, len);
	             baos.flush();
            }
            byte[] bytes = baos.toByteArray();
            
//            Options mOptions = new Options();
//            mOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;
//            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, mOptions);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//            Bitmap bitmap = BitmapFactory.decodeStream(input);		//听说会在一些android版本上有问题，所以修改成这样的方式
//            if (bitmap == null) {
//                throw new IOException("Image could not be decoded");
//            }
            return bytes;
        } 
        catch(OutOfMemoryError e)
        {
        	e.printStackTrace();
        	System.gc();
        	return null;
        }
        finally {
            input.close();
        }
    }
}
