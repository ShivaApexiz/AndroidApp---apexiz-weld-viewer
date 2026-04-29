(function(){
  function syncMobileNav(panel){
    document.querySelectorAll('.mobile-nav-btn').forEach(btn=>{
      btn.classList.toggle('active', btn.dataset.panel === panel);
    });
  }

  const originalShowPanel = window.showPanel;
  if(typeof originalShowPanel === 'function'){
    window.showPanel = async function(panel){
      const result = await originalShowPanel(panel);
      syncMobileNav(panel);
      return result;
    };
  }

  const originalRouteUser = window.routeUser;
  if(typeof originalRouteUser === 'function'){
    window.routeUser = function(profile){
      const result = originalRouteUser(profile);
      if(profile && profile.role === 'engineer') syncMobileNav('overview');
      return result;
    };
  }

  window.addEventListener('load', ()=>{
    const active = document.querySelector('.sidebar-btn.active');
    if(active && active.id && active.id.startsWith('nav-')){
      syncMobileNav(active.id.replace('nav-',''));
    }
  });
})();
