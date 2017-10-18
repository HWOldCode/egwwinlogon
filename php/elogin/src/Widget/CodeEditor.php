<?php

/**
 * ELogin - Egroupware
 * @link http://www.hw-softwareentwicklung.de
 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
 * @package elogin
 * @copyright (c) 2012-16 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
 * @version $Id$
 */

namespace EGroupware\Elogin\Widget;

use EGroupware\Api\Etemplate;

/**
 * CodeEditor
 */
class CodeEditor extends Etemplate\Widget {

	/**
	 * validate
	 * @param type $cname
	 * @param array $expand
	 * @param array $content
	 * @param type $validated
	 */
	public function validate($cname, array $expand, array $content, &$validated=array()) {
		$form_name = self::form_name($cname, $this->id, $expand);
		$value = self::get_array($content, $form_name);
		$valid =& self::get_array($validated, $form_name, true);
		$valid = $value;
	}
}

/**
 * register widget
 */
Etemplate\Widget::registerWidget(
	'EGroupware\Elogin\Widget\CodeEditor',
	array('elogin-code-editor')
	);