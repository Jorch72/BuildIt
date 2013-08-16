package au.com.mineauz.BuildIt.commands.generation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GeneratorCommand
{
	String name();
	
	String perm();
	
	String description() default "";
	
	Class<?>[] args() default {}; 
}
