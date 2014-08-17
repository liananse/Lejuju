/*-
 * Copyright (C) 2011 Google Inc.
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

import java.util.LinkedHashMap;
import java.util.Map;

import net.tsz.afinal.bitmap.core.BitmapDisplayConfig;
import net.tsz.afinal.bitmap.core.DiskCache;
import net.tsz.afinal.bitmap.core.BytesBufferPool.BytesBuffer;
import net.tsz.afinal.utils.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * An LRU {@link Bitmap} cache.
 */
class BitmapCache<K> extends LinkedHashMap<K, Bitmap> {

    
    // Assume a 32-bit image
    private static final long BYTES_PER_PIXEL = 4;

    private static final int INITIAL_CAPACITY = 20;

    private static final float LOAD_FACTOR = 0.75f;

    private final long mMaxBytes;

    private boolean mRemove;
    
    /**
     * Constructor.
     *
     * @param maxBytes the maximum size of the cache in bytes.
     */
    public BitmapCache(long maxBytes) {
        super(INITIAL_CAPACITY, LOAD_FACTOR, true);
        mMaxBytes = maxBytes;
    }

    static long sizeOf(Bitmap b) {
        return b.getWidth() * b.getHeight() * BYTES_PER_PIXEL;
    }

    private static long sizeOf(Iterable<Bitmap> bitmaps) {
        long total = 0;
        for (Bitmap bitmap : bitmaps) {
            total += sizeOf(bitmap);
        }
        Log.d("BitmapCache", "cache total="+total);
        return total;
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, Bitmap> eldest) {
    	if(mRemove)
    	{
    		Log.d("BitmapCache", "save to disk");
//    		Bitmap bitmap = eldest.getValue();
//    		bitmap.recycle();
//    		new FileCache.SaveToFileTask().execute(eldest.getKey(),eldest.getValue());
    	}
        return mRemove;
    }

    /**
     * Removes the eldest entry if the cache is too big.
     */
    private void trimEldest() {
        // Remove null otherwise put() may have no effect
        super.remove(null);

        // Induce LinkedHashMap to remove the eldest element
        mRemove = true;
        try {
            super.put(null, null);
        } finally {
            mRemove = false;
        }

        // Remove null so that it does not appear in the key/entry sets
        super.remove(null);
    }

    /**
     * Removes additional elements until the cache is an acceptable size.
     * <p>
     * This method must be called after each insertion operation.
     */
    private void trim() {
        // This runtime performance of this method is not great,
        // but it's less error-prone than maintaining a counter.
        while (sizeOf(values()) > mMaxBytes) {
            trimEldest();
        }
    }

    private NullPointerException nullKeyException() {
        // Null keys are not permitted because null is used by trim()
        return new NullPointerException("Key is null");
    }

    @Override
    public Bitmap put(K key, Bitmap value) {
        if (key == null) {
            throw nullKeyException();
        }
        try {
        	Log.d("BitmapCache", "put cache="+key);
            return super.put(key, value);
        } finally {
        	Log.d("BitmapCache", "size="+values().size());
            trim();
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends Bitmap> map) {
        if (map.containsKey(null)) {
            throw nullKeyException();
        }
        try {
            super.putAll(map);
        } finally {
            trim();
        }
    }

    @Override
    public Bitmap get(Object key) {
    	Bitmap bitmap = null;
        if (key == null) {
            throw nullKeyException();
        }
        if(super.get(key)!= null)
        {
        	Log.d("BitmapCache", "get cache="+key);
        	bitmap = super.get(key);
        }
//        else
//        {
//        	Log.d("BitmapCache", "get disk="+key);
//        	bitmap = FileCache.getBitmapFromFile((String)key);
//        }
        return bitmap;
    }
    
    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            throw nullKeyException();
        }
        return super.containsKey(key);
    }

    @Override
    public Bitmap remove(Object key) {
        if (key == null) {
            throw nullKeyException();
        }
        return super.remove(key);
    }
}
