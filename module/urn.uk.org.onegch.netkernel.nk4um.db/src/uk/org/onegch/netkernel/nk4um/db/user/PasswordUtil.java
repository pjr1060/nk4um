/*
 * Copyright (C) 2010-2011 by Chris Cormack
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.org.onegch.netkernel.nk4um.db.user;

import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.nkf.NKFException;
import org.netkernel.layer0.representation.IHDSNode;
import org.netkernel.layer0.representation.impl.HDSBuilder;
import org.netkernelroc.mod.layer2.ArgByValue;
import org.netkernelroc.mod.layer2.DatabaseUtil;

public class PasswordUtil {
  private PasswordUtil() {}

  public static String generateSaltedPassword(DatabaseUtil util, String password, String uid, String siteSalt) throws NKFException {
    String userSalt= util.issueSourceRequest("active:sha512",
                                             String.class,
                                             new ArgByValue("operand", uid + siteSalt));
    String saltedPasswordHash= util.issueSourceRequest("active:sha512",
                                                       String.class,
                                                       new ArgByValue("operand", userSalt + password));

    return saltedPasswordHash;
  }

  public static String generateUnsaltedPassword(DatabaseUtil util, String password) throws NKFException {
    String unsaltedPasswordHash= util.issueSourceRequest("active:sha512",
                                                         String.class,
                                                         new ArgByValue("operand", password));
    return unsaltedPasswordHash;
  }

  public static String generateLegacyPassword(DatabaseUtil util, String password) throws NKFException {
    String legacyPasswordHash= util.issueSourceRequest("active:md5",
                                                         String.class,
                                                         new ArgByValue("operand", password));
    return legacyPasswordHash;
  }

  public static void updatePassword(DatabaseUtil util, String password, String uid, String siteSalt) throws NKFException {
    String encryptedPassword= generateSaltedPassword(util, password, uid, siteSalt);

    String sql= "UPDATE nk4um_user_account\n" +
                "SET    password=?\n" +
                "WHERE  id=?;";

    util.issueSourceRequest("active:sqlPSUpdate",
                            null,
                            new ArgByValue("operand", sql),
                            new ArgByValue("param", encryptedPassword),
                            new ArgByValue("param", uid));
  }
}
