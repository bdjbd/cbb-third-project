/**
 * @license Copyright (c) 2003-2013, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	// config.uiColor = '#AADC6E';
  //config.filebrowserBrowseUrl='/common/ckeditor/browse.jsp';
  config.filebrowserImageUploadUrl='/common/ckeditor/upload.jsp?type=Image';
  config.filebrowserFlashUploadUrl='/common/ckeditor/upload.jsp?type=Flash';
  config.toolbar_Sample = [
    ['FontSize'],
    ['Bold','Italic','Underline','Strike','-','TextColor','BGColor','-','RemoveFormat'],
    ['NumberedList','BulletedList','-','Outdent','Indent','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
    ['Maximize', 'ShowBlocks']
  ];
  config.toolbar_Unit = [
    ['Source','-','Undo', 'Redo'],
    ['FontSize'],
    ['Bold','Italic','Underline','Strike','-','TextColor','BGColor','-','RemoveFormat'],
    ['NumberedList','BulletedList','-','Outdent','Indent','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
    ['Link','Unlink'],
    ['Image','Table', 'HorizontalRule', 'SpecialChar'],
    ['Maximize', 'ShowBlocks']
  ];
  config.toolbar_CMS = [
    ['Source','-','PasteText', 'PasteFromWord', '-','Undo', 'Redo'],
    ['FontSize'],
    ['Bold','Italic','Underline', '-','TextColor','BGColor','-','RemoveFormat'],
    ['NumberedList','BulletedList','-','Outdent','Indent','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
    ['Link','Unlink'],
    ['Image','Table', 'HorizontalRule', 'SpecialChar'],
    ['Maximize', 'ShowBlocks']
  ];
};
