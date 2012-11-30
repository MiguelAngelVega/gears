/*
 * gears
 *     http://www.open-logics.com
 *     Copyright (C) 2012, OpenLogics
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package google.guice.aop1;

/**
 * @author Miguel Vega
 * @version $Id: RoleValidationInterceptor.java 0, 2012-11-30 6:09 PM mvega $
 */

import com.google.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class RoleValidationInterceptor implements MethodInterceptor {

    @Inject
    private UserManager userManager;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Role requiredRole =
                invocation.getMethod().getAnnotation(RequiresRole.class).value();

        if (userManager.getCurrentUser() == null ||
                !userManager.getCurrentUser().getRoles().contains(requiredRole)) {

            throw new IllegalStateException("User requires role " + requiredRole);
        }

        return invocation.proceed();
    }
}