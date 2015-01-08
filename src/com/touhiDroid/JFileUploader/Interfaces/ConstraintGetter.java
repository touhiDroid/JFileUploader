/**
 * 
 */
package com.touhiDroid.JFileUploader.Interfaces;

import com.touhiDroid.JFileUploader.models.ServerConstraint;

/**
 * @author Touhid
 *
 */
public interface ConstraintGetter {
	
	public ServerConstraint getServerConstraints();
	public void setServerConstraints(ServerConstraint serverConstraint);

}
