package gr.uom.java.ast;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import java.util.*;

public class SystemObject {

    private List<ClassObject> classList;
    //Map that has as key the classname and as value
    //the position of className in the classNameList
    private Map<String, Integer> classNameMap;
    private Map<MethodInvocationObject, FieldInstructionObject> getterMap;
    private Map<MethodInvocationObject, FieldInstructionObject> setterMap;
    private Map<MethodInvocationObject, FieldInstructionObject> collectionAdderMap;

    public SystemObject() {
        this.classList = new ArrayList<ClassObject>();
        this.classNameMap = new HashMap<String, Integer>();
        this.getterMap = new LinkedHashMap<MethodInvocationObject, FieldInstructionObject>();
        this.setterMap = new LinkedHashMap<MethodInvocationObject, FieldInstructionObject>();
        this.collectionAdderMap = new LinkedHashMap<MethodInvocationObject, FieldInstructionObject>();
    }

    public void addClass(ClassObject c) {
        classNameMap.put(c.getName(),classList.size());
        classList.add(c);
    }
    
    public void addClasses(List<ClassObject> classObjects) {
    	for(ClassObject classObject : classObjects)
    		addClass(classObject);
    }
    
    public void replaceClass(ClassObject c) {
    	int position = getPositionInClassList(c.getName());
    	if(position != -1) {
    		classList.set(position, c);
    	}
    	else {
    		addClass(c);
    	}
    }
    
    public void removeClasses(IFile file) {
    	List<ClassObject> classesToBeRemoved = new ArrayList<ClassObject>();
    	for(ClassObject classObject : classList) {
    		if(classObject.getIFile().equals(file))
    			classesToBeRemoved.add(classObject);
    	}
    	for(ClassObject classObject : classesToBeRemoved) {
    		removeClass(classObject);
    	}
    }
    
    public void removeClass(ClassObject c) {
    	int position = getPositionInClassList(c.getName());
    	if(position != -1) {
    		for(int i=position+1; i<classList.size(); i++) {
    			ClassObject classObject = classList.get(i);
    			classNameMap.put(classObject.getName(), classNameMap.get(classObject.getName())-1);
    		}
    		classNameMap.remove(c.getName());
    		classList.remove(c);
    	}
    }
    
    public void addGetter(MethodInvocationObject methodInvocation, FieldInstructionObject fieldInstruction) {
    	getterMap.put(methodInvocation, fieldInstruction);
    }
    
    public void addSetter(MethodInvocationObject methodInvocation, FieldInstructionObject fieldInstruction) {
    	setterMap.put(methodInvocation, fieldInstruction);
    }
    
    public void addCollectionAdder(MethodInvocationObject methodInvocation, FieldInstructionObject fieldInstruction) {
    	collectionAdderMap.put(methodInvocation, fieldInstruction);
    }
    
    public FieldInstructionObject containsGetter(MethodInvocationObject methodInvocation) {
    	return getterMap.get(methodInvocation);
    }
    
    public FieldInstructionObject containsSetter(MethodInvocationObject methodInvocation) {
    	return setterMap.get(methodInvocation);
    }
    
    public FieldInstructionObject containsCollectionAdder(MethodInvocationObject methodInvocation) {
    	return collectionAdderMap.get(methodInvocation);
    }
    
    public MethodObject getMethod(MethodInvocationObject mio) {
    	ClassObject classObject = getClassObject(mio.getOriginClassName());
    	if(classObject != null)
    		return classObject.getMethod(mio);
    	return null;
    }

    public MethodObject getMethod(SuperMethodInvocationObject smio) {
    	ClassObject classObject = getClassObject(smio.getOriginClassName());
    	if(classObject != null)
    		return classObject.getMethod(smio);
    	return null;
    }

    public boolean containsMethodInvocation(MethodInvocationObject methodInvocation, ClassObject excludedClass) {
    	for(ClassObject classObject : classList) {
    		if(!excludedClass.equals(classObject) && classObject.containsMethodInvocation(methodInvocation))
    			return true;
    	}
    	return false;
    }

    public boolean containsFieldInstruction(FieldInstructionObject fieldInstruction, ClassObject excludedClass) {
    	for(ClassObject classObject : classList) {
    		if(!excludedClass.equals(classObject) && classObject.containsFieldInstruction(fieldInstruction))
    			return true;
    	}
    	return false;
    }

    public boolean containsSuperMethodInvocation(SuperMethodInvocationObject superMethodInvocation) {
    	for(ClassObject classObject : classList) {
    		if(classObject.containsSuperMethodInvocation(superMethodInvocation))
    			return true;
    	}
    	return false;
    }

    public ClassObject getClassObject(String className) {
        Integer pos = classNameMap.get(className);
        if(pos != null)
            return getClassObject(pos);
        else
            return null;
    }

    public ClassObject getClassObject(int pos) {
        return classList.get(pos);
    }

    public ListIterator<ClassObject> getClassListIterator() {
        return classList.listIterator();
    }

    public int getClassNumber() {
        return classList.size();
    }

    public int getPositionInClassList(String className) {
        Integer pos = classNameMap.get(className);
        if(pos != null)
            return pos;
        else
            return -1;
    }

    public Set<ClassObject> getClassObjects() {
    	Set<ClassObject> classObjectSet = new LinkedHashSet<ClassObject>();
    	classObjectSet.addAll(classList);
    	return classObjectSet;
    }


    public Set<ClassObject> getClassObjects(IPackageFragment packageFragment) {
    	Set<ClassObject> classObjectSet = new LinkedHashSet<ClassObject>();
    	try {
    		ICompilationUnit[] packageCompilationUnits = packageFragment.getCompilationUnits();
			for(ICompilationUnit iCompilationUnit : packageCompilationUnits) {
				classObjectSet.addAll(getClassObjects(iCompilationUnit));
			}
    	} catch(JavaModelException e) {
			e.printStackTrace();
		}
    	return classObjectSet;
    }

    public Set<ClassObject> getClassObjects(ICompilationUnit compilationUnit) {
    	Set<ClassObject> classObjectSet = new LinkedHashSet<ClassObject>();
		try {
			IType[] topLevelTypes = compilationUnit.getTypes();
			for(IType type : topLevelTypes) {
				classObjectSet.addAll(getClassObjects(type));
			}
		} catch(JavaModelException e) {
			e.printStackTrace();
		}
    	return classObjectSet;
    }

    public Set<ClassObject> getClassObjects(IType type) {
    	Set<ClassObject> classObjectSet = new LinkedHashSet<ClassObject>();
    	String typeQualifiedName = type.getFullyQualifiedName('.');
    	ClassObject classObject = getClassObject(typeQualifiedName);
    	if(classObject != null)
    		classObjectSet.add(classObject);
    	try {
			IType[] nestedTypes = type.getTypes();
			for(IType nestedType : nestedTypes) {
				classObjectSet.addAll(getClassObjects(nestedType));
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
    	return classObjectSet;
    }



    public List<String> getClassNames() {
        List<String> names = new ArrayList<String>();
        for(int i=0; i<classList.size(); i++) {
            names.add(getClassObject(i).getName());
        }
        return names;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(ClassObject classObject : classList) {
            sb.append(classObject.toString());
            sb.append("\n--------------------------------------------------------------------------------\n");
        }
        return sb.toString();
    }
}