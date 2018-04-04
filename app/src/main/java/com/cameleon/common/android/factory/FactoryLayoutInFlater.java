package com.cameleon.common.android.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.justtennis.R;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.View;
import android.widget.TextView;

public class FactoryLayoutInFlater {

	private static FactoryLayoutInFlater instance;

	@SuppressWarnings("rawtypes")
	static Class       IconMenuItemView_class = null;
	@SuppressWarnings("rawtypes")
	static Constructor IconMenuItemView_constructor = null;

	// standard signature of constructor expected by inflater of all View classes
	@SuppressWarnings("rawtypes")
	private static final Class[] standard_inflater_constructor_signature = new Class[] { Context.class, AttributeSet.class };

	private FactoryLayoutInFlater() {
		
	}
	

	public static FactoryLayoutInFlater getInstance() {
		if (instance==null)
			instance = new FactoryLayoutInFlater();
		return instance;
	}

	/**
	 * http://www.gitshah.com/2011/06/how-to-change-background-color-of.html
	 * http://stackoverflow.com/questions/2944244/change-the-background-color-of-the-options-menu
	 * 
	 * USAGE :
	 * 	@Override
	 *	public boolean onCreateOptionsMenu(Menu menu) {
	 *		....
	 *		setMenuBackground();
	 *		....
	 *	}
	 *	// Sets the menu items background.
	 *  private void setMenuBackground() {
	 *      // Step 1. setting the custom LayoutInflater.Factory instance.
	 *      getLayoutInflater().setFactory(FactoryLayoutInFlater.getInstance().createOptionsMenuBackground(getLayoutInflater()));
	 *  }
	 * 
	 * @return
	 */
	public LayoutInflater.Factory createOptionsMenuBackground() {
		return new Factory()
	    {
	        public View onCreateView(final String name, 
	                                 final Context context,
	                                 final AttributeSet attrs)
	        {
	            if (!name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView"))
	                return null; // use normal inflater

	            View view = null;

	            // "com.android.internal.view.menu.IconMenuItemView" 
	            // - is the name of an internal Java class 
	            //   - that exists in Android <= 3.2 and possibly beyond
	            //   - that may or may not exist in other Android revs
	            // - is the class whose instance we want to modify to set background etc.
	            // - is the class we want to instantiate with the standard constructor:
	            //     IconMenuItemView(context, attrs)
	            // - this is what the LayoutInflater does if we return null
	            // - unfortunately we cannot just call:
	            //     infl.createView(name, null, attrs);
	            //   here because on Android 3.2 (and possibly later):
	            //   1. createView() can only be called inside inflate(),
	            //      because inflate() sets the context parameter ultimately
	            //      passed to the IconMenuItemView constructor's first arg,
	            //      storing it in a LayoutInflater instance variable.
	            //   2. we are inside inflate(),
	            //   3. BUT from a different instance of LayoutInflater (not infl)
	            //   4. there is no way to get access to the actual instance being used
	            // - so we must do what createView() would have done for us
	            //
	            if (IconMenuItemView_class == null)
	            {
	                try
	                {
	                    IconMenuItemView_class = context.getClassLoader().loadClass(name);
	                }
	                catch (ClassNotFoundException e)
	                {
	                    // this OS does not have IconMenuItemView - fail gracefully
	                    return null; // hack failed: use normal inflater
	                }
	            }
	            if (IconMenuItemView_class == null)
	                return null; // hack failed: use normal inflater

	            if (IconMenuItemView_constructor == null)
	            {
	                try
	                {
	                    IconMenuItemView_constructor = 
	                    IconMenuItemView_class.getConstructor(standard_inflater_constructor_signature);
	                }
	                catch (SecurityException e)
	                {
	                    return null; // hack failed: use normal inflater
	                }
	                catch (NoSuchMethodException e)
	                {
	                    return null; // hack failed: use normal inflater
	                }
	            }
	            if (IconMenuItemView_constructor == null)
	                return null; // hack failed: use normal inflater

	            try
	            {
	                Object[] args = new Object[] { context, attrs };
	                view = (View)(IconMenuItemView_constructor.newInstance(args));
	            }
	            catch (IllegalArgumentException e)
	            {
	                return null; // hack failed: use normal inflater
	            }
	            catch (InstantiationException e)
	            {
	                return null; // hack failed: use normal inflater
	            }
	            catch (IllegalAccessException e)
	            {
	                return null; // hack failed: use normal inflater
	            }
	            catch (InvocationTargetException e)
	            {
	                return null; // hack failed: use normal inflater
	            }
	            if (null == view) // in theory handled above, but be safe... 
	                return null; // hack failed: use normal inflater


	            // apply our own View settings after we get back to runloop
	            // - android will overwrite almost any setting we make now
	            final View v = view;
	            new Handler().post(new Runnable()
	            {
	                public void run()
	                {
	                    v.setBackgroundResource(R.drawable.cameleon_common_selector_option_menu_background);

	                    try
	                    {
	                        // in Android <= 3.2, IconMenuItemView implemented with TextView
	                        // guard against possible future change in implementation
	                        TextView tv = (TextView)v;
	                        tv.setTextColor(context.getResources().getColor(R.color.text_black_default));
	                    }
	                    catch (ClassCastException e)
	                    {
	                        // hack failed: do not set TextView attributes
	                    }
	                }
	            });

	            return view;
	        }
	    };
     }
}
