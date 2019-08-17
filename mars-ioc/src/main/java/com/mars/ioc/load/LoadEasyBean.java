package com.mars.ioc.load;

import com.mars.core.constant.MarsConstant;
import com.mars.core.constant.MarsSpace;
import com.mars.core.load.WriteFields;
import com.mars.core.model.MarsBeanModel;
import com.mars.core.load.LoadHelper;
import com.mars.ioc.factory.BeanFactory;

import java.util.List;
import java.util.Map;

/**
 * 加载easyBean
 * @author yuye
 *
 */
public class LoadEasyBean {
	
	/**
	 * 获取全局存储空间 
	 */
	private static MarsSpace constants = MarsSpace.getEasySpace();

	/**
	 * 创建easyBean对象，并完成对象注入
	 */
	public static void loadBean() throws Exception{
		try {
			/* 获取所有的bean数据 */
			List<Map<String,Object>> marsBeansList = LoadHelper.getBeanList();
			
			/* 创建bean对象，并保存起来 */
			Map<String, MarsBeanModel> marsBeanObjects = LoadHelper.getBeanObjectMap();

			for(Map<String,Object> map : marsBeansList) {

				Class<?> cls = (Class<?>)map.get("className");
				String beanName = LoadHelper.getBeanName(map,cls);

				if(marsBeanObjects.get(beanName) == null) {
					MarsBeanModel beanModel = new MarsBeanModel();
					beanModel.setName(beanName);
					beanModel.setCls(cls);
					beanModel.setObj(BeanFactory.createBean(cls));
					marsBeanObjects.put(beanName, beanModel);
				} else {
					throw new Exception("已经存在name为["+beanName+"]的bean了");
				}
			}
			/* 注入对象 */
			iocBean(marsBeanObjects);
		} catch (Exception e) {
			throw new Exception("加载并注入EasyBean的时候出现错误",e);
		} 
	}
	
	/**
	 * easyBean注入
	 * @param marsBeanObjects 对象
	 */
	private static void iocBean(Map<String, MarsBeanModel> marsBeanObjects) throws Exception{
		
		try {
			for(String key : marsBeanObjects.keySet()) {
				MarsBeanModel marsBeanModel = marsBeanObjects.get(key);
				Object obj = marsBeanModel.getObj();
				Class<?> cls = marsBeanModel.getCls();

				/* 获取对象属性，完成注入 */
				WriteFields.writeFields(cls,obj,marsBeanObjects);

				/* 保险起见，重新插入数据 */
				marsBeanModel.setCls(cls);
				marsBeanObjects.put(key, marsBeanModel);
			}
			
			constants.setAttr(MarsConstant.MARS_BEAN_OBJECTS, marsBeanObjects);
		} catch (Exception e) {
			throw new Exception("加载并注入MarsBean的时候出现错误",e);
		} 
	}
}
