package de.dytanic.cloudnet.wrapper.module;

import com.google.common.base.Preconditions;
import de.dytanic.cloudnet.common.language.LanguageManager;
import de.dytanic.cloudnet.common.logging.ILogger;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.Event;
import de.dytanic.cloudnet.driver.event.events.module.*;
import de.dytanic.cloudnet.driver.module.IModuleProvider;
import de.dytanic.cloudnet.driver.module.IModuleProviderHandler;
import de.dytanic.cloudnet.driver.module.IModuleWrapper;
import de.dytanic.cloudnet.driver.module.ModuleDependency;

public final class WrapperModuleProviderHandler implements IModuleProviderHandler {

    @Override
    public boolean handlePreModuleLoad(IModuleWrapper moduleWrapper) {
        boolean cancelled = this.callEvent(new ModulePreLoadEvent(this.getModuleProvider(), moduleWrapper)).isCancelled();
        if (!cancelled) {
            this.getLogger().info(replaceAll(LanguageManager.getMessage("cloudnet-pre-load-module"), this.getModuleProvider(), moduleWrapper));
        }

        return !cancelled;
    }

    @Override
    public void handlePostModuleLoad(IModuleWrapper moduleWrapper) {
        this.callEvent(new ModulePostLoadEvent(this.getModuleProvider(), moduleWrapper));
        this.getLogger().info(replaceAll(LanguageManager.getMessage("cloudnet-post-load-module"), this.getModuleProvider(), moduleWrapper));
    }

    @Override
    public boolean handlePreModuleStart(IModuleWrapper moduleWrapper) {
        boolean cancelled = this.callEvent(new ModulePreStartEvent(this.getModuleProvider(), moduleWrapper)).isCancelled();
        if (!cancelled) {
            this.getLogger().info(replaceAll(LanguageManager.getMessage("cloudnet-pre-start-module"), this.getModuleProvider(), moduleWrapper));
        }

        return !cancelled;
    }

    @Override
    public void handlePostModuleStart(IModuleWrapper moduleWrapper) {
        this.callEvent(new ModulePostStartEvent(this.getModuleProvider(), moduleWrapper));
        this.getLogger().info(replaceAll(LanguageManager.getMessage("cloudnet-post-start-module"), this.getModuleProvider(), moduleWrapper));
    }

    @Override
    public boolean handlePreModuleStop(IModuleWrapper moduleWrapper) {
        boolean cancelled = this.callEvent(new ModulePreStopEvent(this.getModuleProvider(), moduleWrapper)).isCancelled();
        if (!cancelled) {
            this.getLogger().info(replaceAll(LanguageManager.getMessage("cloudnet-pre-stop-module"), this.getModuleProvider(), moduleWrapper));
        }

        return !cancelled;
    }

    @Override
    public void handlePostModuleStop(IModuleWrapper moduleWrapper) {
        this.callEvent(new ModulePostStopEvent(this.getModuleProvider(), moduleWrapper));
        this.getLogger().info(replaceAll(LanguageManager.getMessage("cloudnet-post-stop-module"), this.getModuleProvider(), moduleWrapper));
        CloudNetDriver.getInstance().getEventManager().unregisterListeners(moduleWrapper.getClassLoader());
    }

    @Override
    public void handlePreModuleUnload(IModuleWrapper moduleWrapper) {
        this.callEvent(new ModulePreUnloadEvent(this.getModuleProvider(), moduleWrapper));
        this.getLogger().info(replaceAll(LanguageManager.getMessage("cloudnet-pre-unload-module"), this.getModuleProvider(), moduleWrapper));
    }

    @Override
    public void handlePostModuleUnload(IModuleWrapper moduleWrapper) {
        this.callEvent(new ModulePostUnloadEvent(this.getModuleProvider(), moduleWrapper));
        this.getLogger().info(replaceAll(LanguageManager.getMessage("cloudnet-post-unload-module"), this.getModuleProvider(), moduleWrapper));
    }

    @Override
    public void handlePreInstallDependency(IModuleWrapper moduleWrapper, ModuleDependency dependency) {
        this.callEvent(new ModulePreInstallDependencyEvent(this.getModuleProvider(), moduleWrapper, dependency));
        this.getLogger().info(replaceAll(LanguageManager.getMessage("cloudnet-pre-install-dependency-module")
                        .replace("%group%", dependency.getGroup())
                        .replace("%name%", dependency.getName())
                        .replace("%version%", dependency.getVersion())
                , this.getModuleProvider(), moduleWrapper));
    }

    @Override
    public void handlePostInstallDependency(IModuleWrapper moduleWrapper, ModuleDependency dependency) {
        this.callEvent(new ModulePostInstallDependencyEvent(this.getModuleProvider(), moduleWrapper, dependency));
        this.getLogger().info(replaceAll(LanguageManager.getMessage("cloudnet-post-install-dependency-module")
                        .replace("%group%", dependency.getGroup())
                        .replace("%name%", dependency.getName())
                        .replace("%version%", dependency.getVersion())
                , this.getModuleProvider(), moduleWrapper));
    }

    private ILogger getLogger() {
        return CloudNetDriver.getInstance().getLogger();
    }

    private IModuleProvider getModuleProvider() {
        return CloudNetDriver.getInstance().getModuleProvider();
    }

    private <T extends Event> T callEvent(T event) {
        return CloudNetDriver.getInstance().getEventManager().callEvent(event);
    }

    private String replaceAll(String text, IModuleProvider moduleProvider, IModuleWrapper moduleWrapper) {
        Preconditions.checkNotNull(text);
        Preconditions.checkNotNull(moduleProvider);
        Preconditions.checkNotNull(moduleWrapper);

        return text.replace("%module_group%", moduleWrapper.getModuleConfiguration().getGroup())
                .replace("%module_name%", moduleWrapper.getModuleConfiguration().getName())
                .replace("%module_version%", moduleWrapper.getModuleConfiguration().getVersion())
                .replace("%module_author%", moduleWrapper.getModuleConfiguration().getAuthor());
    }
}