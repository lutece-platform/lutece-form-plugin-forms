package fr.paris.lutece.plugins.forms.business;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

public interface IFormCategoryDAO {

	/**
	 * {@inheritDoc }
	 */
	void insert(FormCategory formCategory, Plugin plugin);

	/**
	 * {@inheritDoc }
	 */
	FormCategory load(int nKey, Plugin plugin);

	/**
	 * {@inheritDoc }
	 */
	void delete(int nKey, Plugin plugin);

	/**
	 * {@inheritDoc }
	 */
	void store(FormCategory formCategory, Plugin plugin);

	/**
	 * {@inheritDoc }
	 */
	List<FormCategory> selectFormCategoryList(Plugin _plugin);

}
