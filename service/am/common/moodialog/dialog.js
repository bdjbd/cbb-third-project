/*Overlay*/
var Overlay = new Class({
	Implements: [Options, Events],
	options: {
		id: 'overlay',
		color: '#000',
		duration: 500,
		opacity: 0.5,
		zIndex: 5000/*,
		onClick: $empty,
		onClose: $empty,
		onHide: $empty,
		onOpen: $empty,
		onShow: $empty
		*/
	},
	initialize: function(container, options){
		this.setOptions(options);
		this.container = document.id(container);
		this.bound = {
			'window': {
				resize: this.resize.bind(this),
				scroll: this.scroll.bind(this)
			},
			overlayClick: this.overlayClick.bind(this),
			tweenStart: this.tweenStart.bind(this),
			tweenComplete: this.tweenComplete.bind(this)
		};
		this.build().attach();
	},
	build: function(){
	  this.overlay = new Element('div', {
			id: this.options.id,
			opacity: 0,
			styles: {
				position: (Browser.ie6) ? 'absolute' : 'fixed',
				background: this.options.color,
				left: 0,
				top: 0,
				'z-index': this.options.zIndex
			}
		}).inject(this.container);
		this.tween = new Fx.Tween(this.overlay, {
			duration: this.options.duration,
			link: 'cancel',
			property: 'opacity'
		});
	 return this;
	}.protect(),
	attach: function(){
		window.addEvents(this.bound.window);
		this.overlay.addEvent('click', this.bound.overlayClick);
		this.tween.addEvents({
			onStart: this.bound.tweenStart,
			onComplete: this.bound.tweenComplete
		});
	 return this;
	},
	detach: function(){
		var args = Array.prototype.slice.call(arguments);
		args.each(function(item){
			if(item == 'window') window.removeEvents(this.bound.window);
			if(item == 'overlay') this.overlay.removeEvent('click', this.bound.overlayClick);
		}, this);
		return this;
	},
	overlayClick: function(){
		this.fireEvent('click');
		return this;
	},
	tweenStart: function(){
		this.overlay.setStyles({
			width: '100%',
			height: this.container.getScrollSize().y
		});
	 return this;
	},
	tweenComplete: function(){
		this.fireEvent(this.overlay.get('opacity') == this.options.opacity ? 'show' : 'hide');
		return this;
	},
	open: function(){
		this.fireEvent('open');
		this.tween.start(this.options.opacity);
		return this;
	},
	close: function(){
		this.fireEvent('close');
		this.tween.start(0);
		return this;
	},
	resize: function(){
		this.fireEvent('resize');
		this.overlay.setStyle('height', this.container.getScrollSize().y);
		return this;
	},
	scroll: function(){
		this.fireEvent('scroll');
		if (Browser.ie6) this.overlay.setStyle('left', window.getScroll().x);
		return this;
	}
});
/*MooDialog*/
var MooDialog = new Class({
	Implements: [Options, Events],
	options: {
		'class': 'MooDialog',
		title: null,
		scroll: true, // IE
		forceScroll: false,
		useEscKey: true,
		destroyOnHide: true,
		autoOpen: true,
		closeButton: true,
		onInitialize: function(){
			this.wrapper.setStyle('display', 'none');
		},
		onBeforeOpen: function(){
			this.wrapper.setStyle('display', 'block');
			this.fireEvent('show');
		},
		onBeforeClose: function(){
			this.wrapper.setStyle('display', 'none');
			this.fireEvent('hide');
		}/*,
		onOpen: function(){},
		onClose: function(){},
		onShow: function(){},
		onHide: function(){}*/
	},
	initialize: function(options){
		this.setOptions(options);
		this.options.inject = this.options.inject || document.body;
		options = this.options;
		var wrapper = this.wrapper = new Element('div.' + options['class'].replace(' ', '.')).inject(options.inject);
		this.content = new Element('div.content').inject(wrapper);
		if (options.title){
			this.title = new Element('div.title').set('text', options.title).inject(wrapper);
			wrapper.addClass('MooDialogTitle');
		}
		if (options.closeButton){
			this.closeButton = new Element('a.close', {
				events: {click: this.close.bind(this)}
			}).inject(wrapper);
		}
		/*<ie6>*/// IE 6 scroll
		if ((options.scroll && Browser.ie6) || options.forceScroll){
			wrapper.setStyle('position', 'absolute');
			var position = wrapper.getPosition(options.inject);
			window.addEvent('scroll', function(){
				var scroll = document.getScroll();
				wrapper.setPosition({
					x: position.x + scroll.x,
					y: position.y + scroll.y
				});
			});
		}
		/*</ie6>*/
		if (options.useEscKey){
			// Add event for the esc key
			document.addEvent('keydown', function(e){
				if (e.key == 'esc') this.close();
			}.bind(this));
		}
		this.addEvent('hide', function(){
			if (options.destroyOnHide) this.destroy();
		}.bind(this));
		this.fireEvent('initialize', wrapper);
	},
	setContent: function(){
		var content = Array.from(arguments);
		if (content.length == 1) content = content[0];

		this.content.empty();

		var type = typeOf(content);
		if (['string', 'number'].contains(type)) this.content.set('text', content);
		else this.content.adopt(content);

		return this;
	},
	open: function(){
		this.fireEvent('beforeOpen', this.wrapper).fireEvent('open');
		this.opened = true;
		return this;
	},
	close: function(){
		this.fireEvent('beforeClose', this.wrapper).fireEvent('close');
		this.opened = false;
		return this;
	},
	destroy: function(){
		this.wrapper.destroy();
	},
	toElement: function(){
		return this.wrapper;
	}
});
Element.implement({
	MooDialog: function(options){
		this.store('MooDialog',
			new MooDialog(options).setContent(this).open()
		);
		return this;
	}
});
/*MooDialog.Fx*/
MooDialog.implement('options', {
	duration: 400,
	closeOnOverlayClick: true,
	onInitialize: function(wrapper){
		this.fx = new Fx.Tween(wrapper, {
			property: 'opacity',
			duration: this.options.duration
		}).set(0);
		this.overlay = new Overlay(this.options.inject, {
			duration: this.options.duration
		});
		if (this.options.closeOnOverlayClick) this.overlay.addEvent('click', this.close.bind(this));
	},
	onBeforeOpen: function(wrapper){
		this.overlay.open();
		this.fx.start(1).chain(function(){
			this.fireEvent('show');
		}.bind(this));
	},
	onBeforeClose: function(wrapper){
		this.overlay.close();
		this.fx.start(0).chain(function(){
			this.fireEvent('hide');
		}.bind(this));
	}
});
/*MooDialog.Alert*/
MooDialog.Alert = new Class({
	Extends: MooDialog,
	options: {
		okText: 'Ok',
		focus: true,
		textPClass: 'MooDialogAlert'
	},
	initialize: function(msg, options){
		this.parent(options);
		var okButton = new Element('input[type=button]', {
			events: {
				click: this.close.bind(this)
			},
			value: this.options.okText
		});
		this.setContent(
			new Element('p.' + this.options.textPClass, {text: msg}),
			new Element('div.buttons').adopt(okButton)
		);
		if (this.options.autoOpen) this.open();
		if (this.options.focus) this.addEvent('show', function(){
			okButton.focus()
		});
	}
});
/*MooDialog.Confirm*/
MooDialog.Confirm = new Class({
	Extends: MooDialog,
	options: {
		okText: 'Ok',
		cancelText: 'Cancel',
		focus: true,
		textPClass: 'MooDialogConfirm'
	},
	initialize: function(msg, fn, fn1, options){
		this.parent(options);
		var emptyFn = function(){},
			self = this;
		var buttons = [
			{fn: fn || emptyFn, txt: this.options.okText},
			{fn: fn1 || emptyFn, txt: this.options.cancelText}
		].map(function(button){
			return new Element('input[type=button]', {
				events: {
					click: function(){
						button.fn();
						self.close();
					}
				},
				value: button.txt
			});
		});
		this.setContent(
			new Element('p.' + this.options.textPClass, {text: msg}),
			new Element('div.buttons').adopt(buttons)
		);
		if (this.options.autoOpen) this.open();
		if(this.options.focus) this.addEvent('show', function(){
			buttons[1].focus();
		});
	}
});
Element.implement({
	confirmLinkClick: function(msg, options){
		this.addEvent('click', function(e){
			e.stop();
			new MooDialog.Confirm(msg, function(){
				location.href = this.get('href');
			}.bind(this), null, options)
		});
		return this;
	},
	confirmFormSubmit: function(msg, options){
		this.addEvent('submit', function(e){
			e.stop();
			new MooDialog.Confirm(msg, function(){
				this.submit();
			}.bind(this), null, options)
		}.bind(this));
		return this;
	}
});
/*MooDialog.Error*/
MooDialog.Error = new Class({
	Extends: MooDialog.Alert,
	options: {
		textPClass: 'MooDialogError'
	}
});
/*MooDialog.Prompt*/
MooDialog.Prompt = new Class({
	Extends: MooDialog,
	options: {
		okText: 'Ok',
		focus: true,
		textPClass: 'MooDialogPrompt'
	},
	initialize: function(msg, fn, options){
		this.parent(options);
		if (!fn) fn = function(){};
		var textInput = new Element('input.textInput', {type: 'text'}),
			submitButton = new Element('input[type=submit]', {value: this.options.okText}),
			formEvents = {
				submit: function(e){
					e.stop();
					fn(textInput.get('value'));
					this.close();
				}.bind(this)
			};
		this.setContent(
			new Element('p.' + this.options.textPClass, {text: msg}),
			new Element('form.buttons', {events: formEvents}).adopt(textInput, submitButton)
		);
		if (this.options.autoOpen) this.open();
		if (this.options.focus) this.addEvent('show', function(){
			textInput.focus();
		});
	}
});
/*MooDialog.IFrame*/
MooDialog.Iframe = new Class({
	Extends: MooDialog,
	options: {
		useScrollBar: true
	},
	initialize: function(url, options){
		this.parent(options);
		this.setContent(
			new Element('iframe', {
				src: url,
				frameborder: 0,
				scrolling: this.options.useScrollBar ? 'auto' : 'no'
			})
		);
		if (this.options.autoOpen) this.open();
    }
});
/*MooDialog.Request*/
MooDialog.Request = new Class({
	Extends: MooDialog,
	initialize: function(url, requestOptions, options){
		this.parent(options);
		this.requestOptions = requestOptions || {};
		this.addEvent('open', function(){
			var request = new Request.HTML(this.requestOptions).addEvent('success', function(text){
				this.setContent(text);
			}.bind(this)).send({
				url: url
			});
		}.bind(this));
		if (this.options.autoOpen) this.open();
    },
	setRequestOptions: function(options){
		this.requestOptions = Object.merge(this.requestOptions, options);
		return this;
	}
});