package com.han.fakeNowcoder.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

// @Component
// @Aspect
public class AlphaAspect {

  @Pointcut("execution(* com.han.fakeNowcoder.service.*.*(..))")
  public void pointcut() {}

  @Before("pointcut()")
  public void before() {
    System.out.println("Before");
  }

  @After("pointcut()")
  public void after() {
    System.out.println("After");
  }

  @AfterReturning("pointcut()")
  public void afterReturning() {
    System.out.println("AfterReturning");
  }

  @AfterThrowing("pointcut()")
  public void afterThrowing() {
    System.out.println("AfterThrowing");
  }

  @Around("pointcut()")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    System.out.println("Around before");
    Object obj = joinPoint.proceed();
    System.out.println("Around after");
    return obj;
  }
}
