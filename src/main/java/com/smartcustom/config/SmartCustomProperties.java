package com.smartcustom.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 智能客服系统自定义配置属性
 * 
 * @author SmartCustom Team
 */
@Component
@ConfigurationProperties(prefix = "smartcustom")
public class SmartCustomProperties {
    
    /**
     * 插件配置
     */
    private Plugin plugin = new Plugin();
    
    /**
     * 工具配置
     */
    private Tool tool = new Tool();
    
    /**
     * 聊天配置
     */
    private Chat chat = new Chat();
    
    /**
     * API配置
     */
    private Api api = new Api();
    
    public static class Plugin {
        /**
         * 是否启用插件
         */
        private boolean enabled = true;
        
        /**
         * 插件扫描包
         */
        private String[] scanPackages = {"com.smartcustom.tool"};
        
        /**
         * 是否启用热更新
         */
        private boolean hotReload = true;
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public String[] getScanPackages() {
            return scanPackages;
        }
        
        public void setScanPackages(String[] scanPackages) {
            this.scanPackages = scanPackages;
        }
        
        public boolean isHotReload() {
            return hotReload;
        }
        
        public void setHotReload(boolean hotReload) {
            this.hotReload = hotReload;
        }
    }
    
    public static class Tool {
        /**
         * 是否启用工具
         */
        private boolean enabled = true;
        
        /**
         * 工具超时时间（毫秒）
         */
        private long timeout = 30000;
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public long getTimeout() {
            return timeout;
        }
        
        public void setTimeout(long timeout) {
            this.timeout = timeout;
        }
    }
    
    public static class Chat {
        /**
         * 最大聊天历史记录数
         */
        private int maxHistory = 20;
        
        /**
         * 会话超时时间（毫秒）
         */
        private long sessionTimeout = 3600000;
        
        public int getMaxHistory() {
            return maxHistory;
        }
        
        public void setMaxHistory(int maxHistory) {
            this.maxHistory = maxHistory;
        }
        
        public long getSessionTimeout() {
            return sessionTimeout;
        }
        
        public void setSessionTimeout(long sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
        }
    }
    
    public static class Api {
        /**
         * API版本
         */
        private String version = "v1";
        
        /**
         * CORS配置
         */
        private Cors cors = new Cors();
        
        public String getVersion() {
            return version;
        }
        
        public void setVersion(String version) {
            this.version = version;
        }
        
        public Cors getCors() {
            return cors;
        }
        
        public void setCors(Cors cors) {
            this.cors = cors;
        }
        
        public static class Cors {
            /**
             * 允许的源
             */
            private String[] allowedOrigins = {"*"};
            
            /**
             * 允许的方法
             */
            private String[] allowedMethods = {"GET", "POST", "PUT", "DELETE", "OPTIONS"};
            
            /**
             * 允许的头部
             */
            private String[] allowedHeaders = {"*"};
            
            /**
             * 是否允许凭证
             */
            private boolean allowCredentials = true;
            
            public String[] getAllowedOrigins() {
                return allowedOrigins;
            }
            
            public void setAllowedOrigins(String[] allowedOrigins) {
                this.allowedOrigins = allowedOrigins;
            }
            
            public String[] getAllowedMethods() {
                return allowedMethods;
            }
            
            public void setAllowedMethods(String[] allowedMethods) {
                this.allowedMethods = allowedMethods;
            }
            
            public String[] getAllowedHeaders() {
                return allowedHeaders;
            }
            
            public void setAllowedHeaders(String[] allowedHeaders) {
                this.allowedHeaders = allowedHeaders;
            }
            
            public boolean isAllowCredentials() {
                return allowCredentials;
            }
            
            public void setAllowCredentials(boolean allowCredentials) {
                this.allowCredentials = allowCredentials;
            }
        }
    }
    
    public Plugin getPlugin() {
        return plugin;
    }
    
    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }
    
    public Tool getTool() {
        return tool;
    }
    
    public void setTool(Tool tool) {
        this.tool = tool;
    }
    
    public Chat getChat() {
        return chat;
    }
    
    public void setChat(Chat chat) {
        this.chat = chat;
    }
    
    public Api getApi() {
        return api;
    }
    
    public void setApi(Api api) {
        this.api = api;
    }
}